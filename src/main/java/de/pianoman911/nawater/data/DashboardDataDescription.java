package de.pianoman911.nawater.data;

import com.google.gson.JsonObject;

import java.util.UUID;

public record DashboardDataDescription(UUID id, String product__slug) {

    public static DashboardDataDescription of(JsonObject json){
        return new DashboardDataDescription(
                UUID.fromString(json.get("id").getAsString()),
                json.get("product__slug").getAsString()
        );
    }
}
