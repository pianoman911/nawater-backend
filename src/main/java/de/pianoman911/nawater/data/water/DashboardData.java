package de.pianoman911.nawater.data.water;

import com.google.gson.JsonArray;

import java.util.List;

public record DashboardData(List<DashboardDataEntry> entries, DashboardDataDescription description) {

    public static DashboardData of(JsonArray data) {
        return new DashboardData(
                DashboardDataEntry.of(data.get(0).getAsJsonObject()),
                DashboardDataDescription.of(data.get(1).getAsJsonArray().get(0).getAsJsonObject())
        );
    }
}
