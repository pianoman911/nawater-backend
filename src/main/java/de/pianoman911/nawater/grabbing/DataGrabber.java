package de.pianoman911.nawater.grabbing;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.pianoman911.nawater.data.Dashboard;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DataGrabber {

    public static final HttpClient CLIENT = HttpClient.newHttpClient();
    public static final Gson GSON = new Gson();

    public static CompletableFuture<Dashboard> request(UUID dashboardId) {
        return CompletableFuture.supplyAsync(() -> {
            String body = "{\"operationName\":\"fetchWorkspaceDashboardData\",\"variables\":{\"dashboardId\":\"" + dashboardId + "\",\"dashboardIdx\":0},\"query\":\"query fetchWorkspaceDashboardData($dashboardId: String!, $dashboardIdx: Int, $dashboardConfig: JSONString) {\\n  dashboard(id: $dashboardId) {\\n    id\\n    dashboardData(dashboard: $dashboardIdx, dashboardConfig: $dashboardConfig)\\n    deviceInformation(dashboardConfig: $dashboardConfig)\\n    __typename\\n  }\\n}\\n\"}";

            // Fake User-Agent
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.datacake.co/graphql/?op=fetchWorkspaceDashboardData"))
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("authorization", "")
                    .header("Cache-Control", "no-cache")
                    .header("content-type", "application/json")
                    .header("DNT", "1")
                    .header("Origin", "https://www.m2mgermany-cockpit.de")
                    .header("Pragma", "no-cache")
                    .header("Referer", "https://www.m2mgermany-cockpit.de/")
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Mode", "cors")
                    .header("Sec-Fetch-Site", "cross-site")
                    .header("Sec-GPC", "1")
                    .header("TE", "trailers")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; rv:106.0) Gecko/20100101 Firefox/106.0")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            try {
                HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                return Dashboard.of(GSON.fromJson(response.body(), JsonObject.class).getAsJsonObject("data").getAsJsonObject("dashboard"));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
