package de.pianoman911.nawater.data.water;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.pianoman911.nawater.NaWater;

import java.util.UUID;

public record Dashboard(String ___typename, DashboardData dashboardData, DeviceInformation deviceInformation, UUID id) {

    public static Dashboard of(JsonObject json) {
        String type = json.get("__typename").getAsString();
        DashboardData dashboardData = DashboardData.of(NaWater.GSON.fromJson(json.get("dashboardData").getAsString().replace("\\", ""), JsonArray.class));
        DeviceInformation deviceInformation = DeviceInformation.of(NaWater.GSON.fromJson(json.get("deviceInformation").getAsString().replace("\\", ""), JsonObject.class), dashboardData.description());
        UUID id = UUID.fromString(json.get("id").getAsString());

        return new Dashboard(type, dashboardData, deviceInformation, id);
    }

    @Override
    public String toString() {
        return "Dashboard{" +
                "___typename='" + ___typename + '\'' +
                ", dashboardData=" + dashboardData +
                ", deviceInformation=" + deviceInformation +
                ", id=" + id +
                '}';
    }
}
