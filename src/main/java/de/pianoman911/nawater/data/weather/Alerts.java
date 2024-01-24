package de.pianoman911.nawater.data.weather;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public record Alerts(
        String sender_name,
        String event,
        long start,
        long end,
        String description,
        String[] tags
) {
    public static Set<Alerts> parse(JsonArray alerts) {
        Set<Alerts> alertSet = new HashSet<>();
        for (int i = 0; i < alerts.size(); i++) {
            JsonObject alert = alerts.get(i).getAsJsonObject();
            JsonArray tagsArray = alert.getAsJsonObject().get("tags").getAsJsonArray();
            String[] tags = new String[tagsArray.size()];
            for (int j = 0; j < tagsArray.size(); j++) {
                tags[j] = tagsArray.get(j).getAsString();
            }

            alertSet.add(new Alerts(
                    alert.getAsJsonObject().get("sender_name").getAsString(),
                    alert.getAsJsonObject().get("event").getAsString(),
                    alert.getAsJsonObject().get("start").getAsLong(),
                    alert.getAsJsonObject().get("end").getAsLong(),
                    alert.getAsJsonObject().get("description").getAsString(),
                    tags
            ));
        }
        return alertSet;
    }

    @Override
    public String toString() {
        return "Alerts{" +
                "sender_name='" + sender_name + '\'' +
                ", event='" + event + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", description='" + description + '\'' +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }
}
