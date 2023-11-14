package de.pianoman911.nawater.web;

import com.sun.net.httpserver.HttpHandler;
import de.pianoman911.nawater.util.StreamUtils;

public class RootHandler implements HttpHandler {

    @Override
    public void handle(com.sun.net.httpserver.HttpExchange exchange) throws java.io.IOException {
        exchange.sendResponseHeaders(200, 0);
        StreamUtils.writeFully("Hello World!",exchange.getResponseBody());

        exchange.getResponseBody().close();
    }
}
