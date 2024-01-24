package de.pianoman911.nawater.data.weather;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public record HourlyWeather(
        long date,
        double temp,
        double feels_like,
        double pressure,
        double humidity,
        double dew_point,
        double uvi,
        double clouds,
        double visibility,
        double wind_speed,
        double wind_deg,
        double wind_gust,
        double pop,
        EasyWeather[] weather
) {


    public static HourlyWeather parseSingle(JsonObject current) {
        return new HourlyWeather(
                current.get("dt").getAsLong(),
                current.get("temp").getAsDouble(),
                current.get("feels_like").getAsDouble(),
                current.get("pressure").getAsDouble(),
                current.get("humidity").getAsDouble(),
                current.get("dew_point").getAsDouble(),
                current.get("uvi").getAsDouble(),
                current.get("clouds").getAsDouble(),
                current.get("visibility").getAsDouble(),
                current.get("wind_speed").getAsDouble(),
                current.get("wind_deg").getAsDouble(),
                current.get("wind_gust").getAsDouble(),
                current.has("pop") ? current.get("pop").getAsDouble() : -1,
                EasyWeather.parse(current.getAsJsonArray("weather"))
        );
    }

    public static Map<Long, HourlyWeather> parse(JsonArray hourly) {
        Map<Long, HourlyWeather> hourlyWeatherMap = new HashMap<>(hourly.size());
        for (int i = 0; i < hourly.size(); i++) {
            JsonObject current = hourly.get(i).getAsJsonObject();
            hourlyWeatherMap.put(current.get("dt").getAsLong(), parseSingle(current));
        }
        return hourlyWeatherMap;
    }

    @Override
    public String toString() {
        return "HourlyWeather{" +
                "date=" + date +
                ", temp=" + temp +
                ", feels_like=" + feels_like +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                ", dew_point=" + dew_point +
                ", uvi=" + uvi +
                ", clouds=" + clouds +
                ", visibility=" + visibility +
                ", wind_speed=" + wind_speed +
                ", wind_deg=" + wind_deg +
                ", wind_gust=" + wind_gust +
                ", pop=" + pop +
                ", weather=" + Arrays.toString(weather) +
                '}';
    }
}
