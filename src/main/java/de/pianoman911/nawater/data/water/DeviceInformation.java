package de.pianoman911.nawater.data.water;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Set;

public record DeviceInformation(String geoFieldName, boolean online, Set<String> tags, String verboseName) {
    public static DeviceInformation of(JsonObject deviceInformation, DashboardDataDescription dashboardDataDescription) {
        JsonObject device = deviceInformation.get(dashboardDataDescription.id().toString()).getAsJsonObject();
        return new DeviceInformation(
                device.get("geoFieldName").getAsString(),
                device.get("online").getAsBoolean(),
                device.getAsJsonArray("tags").asList().stream()
                        .map(JsonElement::getAsString).collect(HashSet::new, Set::add, Set::addAll),
                device.get("verboseName").getAsString()
        );
    }

    @Override
    public String toString() {
        return "DeviceInformation{" +
                "geoFieldName='" + geoFieldName + '\'' +
                ", online=" + online +
                ", tags=" + tags +
                ", verboseName='" + verboseName + '\'' +
                '}';
    }
}
