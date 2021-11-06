package dev.krijninc.slurp;

import dev.krijninc.slurp.entities.DrunkServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DashboardServerConnector {
    private static final String endpoint = ConfigLoader.getString("self-hosted-dashboard-url") != null ? ConfigLoader.getString("self-hosted-dashboard-url") : "https://slurp.deno.dev/v1";

    public static HttpResponse<String> postNoAuth(String path) throws IOException, InterruptedException {
        String postEndpoint = endpoint + path;

        var request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        var client = HttpClient.newHttpClient();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> post(String path) throws IOException, InterruptedException {
        String postEndpoint = endpoint + path;

        var request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Slurp.getDrunkServer().getApiToken())
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        var client = HttpClient.newHttpClient();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> post(String path, String body) throws IOException, InterruptedException {
        String postEndpoint = endpoint + path;

        var request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Slurp.getDrunkServer().getApiToken())
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var client = HttpClient.newHttpClient();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
