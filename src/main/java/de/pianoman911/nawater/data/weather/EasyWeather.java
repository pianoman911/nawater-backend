package de.pianoman911.nawater.data.weather;

import com.google.gson.JsonArray;

public record EasyWeather(
        String description,
        String icon,
        int id,
        String main
) {
    public static EasyWeather[] parse(JsonArray weather) {
        EasyWeather[] easyWeathers = new EasyWeather[weather.size()];
        for (int i = 0; i < weather.size(); i++) {
            easyWeathers[i] = new EasyWeather(
                    weather.get(i).getAsJsonObject().get("description").getAsString(),
                    weather.get(i).getAsJsonObject().get("icon").getAsString(),
                    weather.get(i).getAsJsonObject().get("id").getAsInt(),
                    weather.get(i).getAsJsonObject().get("main").getAsString()
            );
        }
        return easyWeathers;
    }

    @Override
    public String toString() {
        return "EasyWeather{" +
                "description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", id=" + id +
                ", main='" + main + '\'' +
                '}';
    }
}
