package de.pianoman911.nawater.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import de.pianoman911.nawater.NaWater;
import de.pianoman911.nawater.web.api.v1.DataQueryApiV1;
import de.pianoman911.nawater.web.api.v2.DataQueryApiV2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class WebServer extends Thread {

    private final NaWater service;

    public WebServer(NaWater service) {
        this.service = service;
    }

    public static boolean checkCors(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
        exchange.getResponseHeaders().add("Access-Control-Max-Age", "86400");
        try {
            if (exchange.getRequestMethod().equals("OPTIONS")) {

                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return true;
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return false;
    }

    @Override
    public void run() {
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress("0.0.0.0", service.getConfig().web.port), 0);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        server.createContext("/", new RootHandler());
        server.createContext("/api/v1/query", new DataQueryApiV1(service));

        server.createContext("/api/v2/query", new DataQueryApiV2(service));

        AtomicInteger threadCount = new AtomicInteger(1);
        server.setExecutor(Executors.newCachedThreadPool(r -> {
            Thread thread = new Thread(r, "WebServer-" + threadCount.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        }));

        server.start();
    }
}
