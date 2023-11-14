package de.pianoman911.nawater.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record DashboardDataEntry(LocalDateTime capturedAt, double height) {
    public static List<DashboardDataEntry> of(JsonObject data) {
        List<DashboardDataEntry> entries = new ArrayList<>();
        data.asMap().forEach((key, value) -> {
            JsonArray array = value.getAsJsonArray();
            if (array.get(0).isJsonArray()) { // Sometimes the data is not a JsonArray; I don't know why
                array.get(0).getAsJsonArray().forEach(element -> {
                    if (element instanceof JsonArray inner) {
                        entries.add(createEntry(inner));
                    }
                });
            }
        });
        return entries;
    }

    private static DashboardDataEntry createEntry(JsonArray array) {
        if (array.get(1).isJsonNull()) {
            return new DashboardDataEntry(
                    LocalDateTime.parse(array.get(0).getAsString()),
                    -1
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
