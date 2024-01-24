package de.pianoman911.nawater.web.api.v2;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.pianoman911.nawater.NaWater;
import de.pianoman911.nawater.util.StreamUtils;
import de.pianoman911.nawater.web.WebServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataQueryApiV2 implements HttpHandler {

    private final NaWater service;

    public DataQueryApiV2(NaWater service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (WebServer.checkCors(exchange)) {
            return;
        }
        try {
            String[] raw = exchange.getRequestURI().getQuery().split("&");
            Map<String, String> args = new HashMap<>();

            for (String s : raw) {
                String[] split = s.split("=");
                args.put(split[0], split[1]);
            }

            String[] ids = args.get("id").split(";");
            long start = Long.parseLong(args.get("start"));
            long end = Long.parseLong(args.get("end"));
            boolean chartJs = false;
            if (args.containsKey("chartjs")) {
                chartJs = Boolean.parseBoolean(args.get("chartjs"));
            }

            JsonObject object = new JsonObject();

            Map<String, JsonArray> data = new HashMap<>();
            for (String id : ids) {
                data.put(id, this.service.getArchiver().query(id, start, end));
            }

            List<Long> dates = new ArrayList<>();
            for (Map.Entry<String, JsonArray> entry : data.entrySet()) {
                JsonArray values = entry.getValue();
                for (JsonElement element : values) {
                    JsonObject value = element.getAsJsonObject();
                    long timestamp = value.get("timestamp").getAsLong();
                    if (!dates.contains(timestamp)) {
                        dates.add(timestamp);
                    }
                }
            }
            if (chartJs) {
                JsonArray datesArray = new JsonArray(dates.size());
                for (long date : dates) {
                    datesArray.add(date);
                }
                object.add("dates", datesArray);
            }
            for (Map.Entry<String, JsonArray> entry : data.entrySet()) {
                JsonArray values = fixedArray(entry.getValue(), dates);
                if (chartJs) {
                    JsonArray heights = new JsonArray(values.size());
                    for (JsonElement element : values) {
                        JsonObject object1 = element.getAsJsonObject();
                        heights.add(object1.get("height"));
                    }
                    object.add(entry.getKey(), heights);
                } else {
                    for (JsonElement value : values) {
                        object.add(entry.getKey(), value);
                    }
                }
            }

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, 0);
            StreamUtils.writeJsonFully(object, exchange.getResponseBody());
            exchange.getResponseBody().close();
        } catch (Exception exception) {
            exception.printStackTrace();
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().close();
        }
    }

    // Sometimes the values array has no data for a specific date, so we need to fill it with the previous value
    private JsonArray fixedArray(JsonArray values, List<Long> dates) {
        JsonArray array = new JsonArray(dates.size()); // min size
        double lastValue = -1;
        Map<Long, Double> data = new HashMap<>();
        for (JsonElement value : values) {
            JsonObject object = value.getAsJsonObject();
            data.put(object.get("timestamp").getAsLong(), object.get("height").getAsDouble());
        }
        for (long date : dates) {
            JsonObject object = new JsonObject();
            object.addProperty("timestamp", date);

            Double height = data.get(date);
            if (height != null) {
                if (height < 0) {
                    continue;
                }
                lastValue = height;
                object.addProperty("height", lastValue);
            } else {
                object.addProperty("height", lastValue);
                System.out.println("No value for date " + date + ", using last value " + lastValue);
            }
            array.add(object);
        }
        return array;
    }
}
