package de.pianoman911.nawater.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record DashboardDataEntry(LocalDateTime capturedAt, double height) {
    public static List<DashboardDataEntry> of(JsonObject data) {
        List<DashboardDataEntry> entries = new ArrayList<>();

        JsonArray arrayData = null;
        double currentHeight = -1;

        for (Map.Entry<String, JsonElement> entry : data.asMap().entrySet()) {
            JsonArray array = entry.getValue().getAsJsonArray();
            if (array.get(0).isJsonArray()) { // Sometimes the data is not a JsonArray; I don't know why
                arrayData = array.get(0).getAsJsonArray();
            } else {
                currentHeight = array.get(0).getAsDouble();
            }
        }
        for (JsonElement element : arrayData) {
            if (element instanceof JsonArray inner) {
                entries.add(createEntry(inner, currentHeight));
            }
        }

        return entries;
    }

    private static DashboardDataEntry createEntry(JsonArray array, double currentHeight) {
        if (array.get(1).isJsonNull()) {
            System.out.println("Null: " + currentHeight);
            return new DashboardDataEntry(
                    LocalDateTime.parse(array.get(0).getAsString()),
                    currentHeight
            );
        }
        return new DashboardDataEntry(
                LocalDateTime.parse(array.get(0).getAsString()),
                array.get(1).getAsDouble()
        );
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        DashboardDataEntry that = (DashboardDataEntry) object;
        return Double.compare(height, that.height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(height);
    }
}
