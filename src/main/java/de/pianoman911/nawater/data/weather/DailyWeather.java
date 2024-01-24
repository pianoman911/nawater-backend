package de.pianoman911.nawater.data.weather;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public record DailyWeather(
        long date,
        long sunrise,
        long sunset,
        long moonrise,
        long moonset,
        double moon_phase,
        Temp temp,
        FeelsLike feels_like,
        double pressure,
        double humidity,
        double dew_point,
        double uvi,
        double clouds,
        double wind_speed,
        double wind_deg,
        double wind_gust,
        double pop,
        EasyWeather[] weather
) {

    public record Temp(
            double day,
            double min,
            double max,
            double night,
            double eve,
            double morn
    ) {
        public static Temp parse(JsonObject temp) {
            return new Temp(
                    temp.get("day").getAsDouble(),
                    temp.get("min").getAsDouble(),
                    temp.get("max").getAsDouble(),
                    temp.get("night").getAsDouble(),
                    temp.get("eve").getAsDouble(),
                    temp.get("morn").getAsDouble()
            );
        }

        @Override
        public String toString() {
            return "Temp{" +
                    "day=" + day +
                    ", min=" + min +
                    ", max=" + max +
                    ", night=" + night +
                    ", eve=" + eve +
                    ", morn=" + morn +
                    '}';
        }
    }

    public record FeelsLike(
            double day,
            double night,
            double eve,
            double morn
    ) {
        public static FeelsLike parse(JsonObject feels_like) {
            return new FeelsLike(
                    feels_like.get("day").getAsDouble(),
                    feels_like.get("night").getAsDouble(),
                    feels_like.get("eve").getAsDouble(),
                    feels_like.get("morn").getAsDouble()
            );
        }

        @Override
        public String toString() {
            return "FeelsLike{" +
                    "day=" + day +
                    ", night=" + night +
                    ", eve=" + eve +
                    ", morn=" + morn +
                    '}';
        }
    }


    public static DailyWeather parseSingle(JsonObject current) {
        return new DailyWeather(
                current.get("dt").getAsLong(),
                current.get("sunrise").getAsLong(),
                current.get("sunset").getAsLong(),
                current.get("moonrise").getAsLong(),
                current.get("moonset").getAsLong(),
                current.get("moon_phase").getAsDouble(),
                Temp.parse(current.getAsJsonObject("temp")),
                FeelsLike.parse(current.getAsJsonObject("feels_like")),
                current.get("pressure").getAsDouble(),
                current.get("humidity").getAsDouble(),
                current.get("dew_point").getAsDouble(),
                current.get("uvi").getAsDouble(),
                current.get("clouds").getAsDouble(),
                current.get("wind_speed").getAsDouble(),
                current.get("wind_deg").getAsDouble(),
                current.get("wind_gust").getAsDouble(),
                current.get("pop").getAsDouble(),
                EasyWeather.parse(current.getAsJsonArray("weather"))
        );
    }

    public static  Map<Long, DailyWeather> parse(JsonArray hourly) {
        Map<Long, DailyWeather> hourlyWeatherMap = new HashMap<>(hourly.size());
        for (int i = 0; i < hourly.size(); i++) {
            JsonObject current = hourly.get(i).getAsJsonObject();
            hourlyWeatherMap.put(current.get("dt").getAsLong(), parseSingle(current));
        }
        return hourlyWeatherMap;
    }

    @Override
    public String toString() {
        return "DailyWeather{" +
                "date=" + date +
                ", sunrise=" + sunrise +
                ", sunset=" + sunset +
                ", moonrise=" + moonrise +
                ", moonset=" + moonset +
                ", moon_phase=" + moon_phase +
                ", temp=" + temp +
                ", feels_like=" + feels_like +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                ", dew_point=" + dew_point +
                ", uvi=" + uvi +
                ", clouds=" + clouds +
                ", wind_speed=" + wind_speed +
                ", wind_deg=" + wind_deg +
                ", wind_gust=" + wind_gust +
                ", pop=" + pop +
                ", weather=" + Arrays.toString(weather) +
                '}';
    }
}
