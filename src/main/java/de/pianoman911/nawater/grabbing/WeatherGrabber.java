package de.pianoman911.nawater.grabbing;

import com.google.gson.JsonObject;
import de.pianoman911.nawater.NaWater;
import de.pianoman911.nawater.data.weather.OWMOC;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherGrabber {

    public static final HttpClient CLIENT = HttpClient.newHttpClient();

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://openweathermap.org/data/2.5/onecall?lat=50.3167&lon=8.5&units=metric&appid=439d4b804bc8187953eb36d2a8c26a02"))
                .build();

        HttpResponse<String> send = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(send.body());
        OWMOC owmoc = OWMOC.parse(NaWater.GSON.fromJson(send.body(), JsonObject.class));
        System.out.println(owmoc);
    }
}
