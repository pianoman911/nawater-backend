package de.pianoman911.nawater.web;

import com.sun.net.httpserver.HttpServer;
import de.pianoman911.nawater.NaWater;
import de.pianoman911.nawater.web.api.DataQueryApi;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebServer extends Thread {

    private final NaWater service;

    public WebServer(NaWater service) {
        this.service = service;
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
        server.createContext("/api/v1/query", new DataQueryApi(service));

        server.start();
    }
}
