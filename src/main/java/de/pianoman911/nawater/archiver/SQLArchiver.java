package de.pianoman911.nawater.archiver;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.pianoman911.nawater.NaWater;
import de.pianoman911.nawater.config.NaWaterConfig;
import de.pianoman911.nawater.data.Dashboard;
import de.pianoman911.nawater.data.DashboardDataEntry;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

public class SQLArchiver implements Archiver {

    private final MariaDbDataSource dataSource;
    private final LoadingCache<DashboardCacheKey, JsonArray> cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(1, TimeUnit.DAYS)
            .build(key -> query0(key.dashboardId(), key.from(), key.to()));

    public SQLArchiver(NaWater naWater) {
        NaWaterConfig.MySql config = naWater.getConfig().mysql;

        try {
            this.dataSource = new MariaDbDataSource(("jdbc:mariadb://%s:%s/%s?socketTimeout=30000&" +
                    "cachePrepStmts=true&useServerPrepStmts=true")
                    .formatted(config.host, config.port, config.database));
            this.dataSource.setUser(config.username);
            this.dataSource.setPassword(config.password);
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void archive(Dashboard dashboard) {
        try (
                Connection connection = this.dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `nawater_data` (`dashboard_id`, `timestamp`, `height`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `height` = IF(height LIKE '-1', ?, height)")
        ) {
            for (DashboardDataEntry entry : dashboard.dashboardData().entries()) {
                preparedStatement.setString(1, dashboard.id().toString());
                preparedStatement.setLong(2, entry.capturedAt().toInstant(ZoneOffset.UTC).getEpochSecond());
                preparedStatement.setDouble(3, entry.height());
                preparedStatement.setDouble(4, entry.height());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
        this.cache.asMap().keySet().removeIf(key -> {
            if (key.dashboardId().equals(dashboard.id().toString())) {
                long from = key.from;
                long to = key.to;
                for (DashboardDataEntry entry : dashboard.dashboardData().entries()) {
                    long stamp = entry.capturedAt().toInstant(ZoneOffset.UTC).getEpochSecond();
                    if (stamp >= from && stamp <= to) {
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @Override
    public JsonArray query(String dashboardId, long from, long to) {
        return this.cache.get(new DashboardCacheKey(dashboardId, from, to));
    }

    private JsonArray query0(String dashboardId, long from, long to) {
        JsonArray array = new JsonArray();
        try (
                Connection connection = this.dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT `timestamp`, `height` FROM `nawater_data` WHERE `dashboard_id` = ? AND `timestamp` >= ? AND `timestamp` <= ? ORDER BY `timestamp` ASC;")
        ) {
            preparedStatement.setString(1, dashboardId);
            preparedStatement.setLong(2, from);
            preparedStatement.setLong(3, to);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                JsonObject entry = new JsonObject();
                entry.addProperty("timestamp", resultSet.getLong("timestamp"));
                entry.addProperty("height", resultSet.getDouble("height"));
                array.add(entry);
            }

        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
        return array;
    }

    private record DashboardCacheKey(String dashboardId, long from, long to) {
    }
}
