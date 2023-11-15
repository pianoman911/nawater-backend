package de.pianoman911.nawater.web.api;

import com.google.gson.JsonArray;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.pianoman911.nawater.NaWater;
import de.pianoman911.nawater.util.StreamUtils;

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

            JsonArray array = this.service.getArchiver().query(id, start, end);

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, 0);
            StreamUtils.writeJsonFully(array, exchange.getResponseBody());
            exchange.getResponseBody().close();
        } catch (Exception exception) {
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().close();
        }
    }
}
