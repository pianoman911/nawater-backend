package de.pianoman911.nawater.data.weather;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public record OWMOC(double lat,
                    double lon,
                    String timezone,
                    int timezone_offset,
                    Set<Alerts> alerts,
                    HourlyWeather current,
                    Map<Long, Double> minutely,
                    Map<Long, HourlyWeather> hourly,
                    Map<Long, DailyWeather> daily
) {

    public static OWMOC parse(JsonObject object) {
        return new OWMOC(
                object.get("lat").getAsDouble(),
                object.get("lon").getAsDouble(),
                object.get("timezone").getAsString(),
                object.get("timezone_offset").getAsInt(),
                Alerts.parse(object.getAsJsonArray("alerts")),
                HourlyWeather.parseSingle(object.getAsJsonObject("current")),
                parseMinutely(object.getAsJsonArray("minutely")),
                HourlyWeather.parse(object.getAsJsonArray("hourly")),
                DailyWeather.parse(object.getAsJsonArray("daily"))
        );
    }

    private static Map<Long, Double> parseMinutely(JsonArray minutely) {
        Map<Long, Double> minutelyMap = new HashMap<>(minutely.size());
        for (int i = 0; i < minutely.size(); i++) {
            JsonObject current = minutely.get(i).getAsJsonObject();
            minutelyMap.put(current.get("dt").getAsLong(), current.get("precipitation").getAsDouble());
        }
        return minutelyMap;
    }

    @Override
    public String toString() {
        return "OWMOC{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", timezone='" + timezone + '\'' +
                ", timezone_offset=" + timezone_offset +
                ", alerts=" + alerts +
                ", current=" + current +
                ", minutely=" + minutely +
                ", hourly=" + hourly +
                ", daily=" + daily +
                '}';
    }
}
