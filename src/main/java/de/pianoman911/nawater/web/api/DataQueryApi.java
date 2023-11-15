package de.pianoman911.nawater.web.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.pianoman911.nawater.NaWater;
import de.pianoman911.nawater.util.StreamUtils;
import de.pianoman911.nawater.web.WebServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataQueryApi implements HttpHandler {

    private final NaWater service;

    public DataQueryApi(NaWater service) {
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

            String id = args.get("id");
            long start = Long.parseLong(args.get("start"));
            long end = Long.parseLong(args.get("end"));
            boolean chartJs = false;
            if (args.containsKey("chartjs")) {
                chartJs = Boolean.parseBoolean(args.get("chartjs"));
            }

            JsonArray array = this.service.getArchiver().query(id, start, end);

            JsonArray jsInnerX = new JsonArray();
            JsonArray jsInnerY = new JsonArray();
            if (chartJs) {
                for (int i = 0; i < array.size(); i++) {
                    JsonObject object = array.get(i).getAsJsonObject();
                    jsInnerX.add(object.get("timestamp").getAsLong());

                    double height = object.get("height").getAsDouble();
                    if (height < 0) {
                        jsInnerY.add("null");
                    } else {
                        jsInnerY.add(height);
                    }
                }
                array = new JsonArray();
                array.add(jsInnerX);
                array.add(jsInnerY);
            }

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, 0);
            StreamUtils.writeJsonFully(array, exchange.getResponseBody());
            exchange.getResponseBody().close();
        } catch (Exception exception) {
            exception.printStackTrace();
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().close();
        }
    }
}
