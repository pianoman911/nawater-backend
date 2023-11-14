package de.pianoman911.nawater.archiver;

import de.pianoman911.nawater.NaWater;
import de.pianoman911.nawater.config.NaWaterConfig;
import de.pianoman911.nawater.data.Dashboard;
import de.pianoman911.nawater.data.DashboardDataEntry;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZoneOffset;

public class SQLArchiver implements Archiver {

    private final MariaDbDataSource dataSource;

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
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `nawater_data` (`dashboard_id`, `timestamp`, `height`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `height` = `height`;")
        ) {
            for (DashboardDataEntry entry : dashboard.dashboardData().entries()) {
                preparedStatement.setString(1, dashboard.id().toString());
                preparedStatement.setLong(2, entry.capturedAt().toInstant(ZoneOffset.UTC).getEpochSecond());
                preparedStatement.setDouble(3, entry.height());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
