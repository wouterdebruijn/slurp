package dev.krijninc.slurp;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DashboardServerConnector {
    private static final String endpoint = ConfigLoader.getString("self-hosted-dashboard-url").equals("") ? "https://slurp.deno.dev/v1" : ConfigLoader.getString("self-hosted-dashboard-url");

    public static HttpResponse<String> postNoAuth(String path) throws IOException, InterruptedException {
        String postEndpoint = endpoint + path;

        var request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        var client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Slurp.getFancyLogger().info("Requests responed with status code " + response.statusCode());
        return response;
    }

    public static HttpResponse<String> get(String path) throws IOException, InterruptedException {
        String postEndpoint = endpoint + path;

        var request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .build();

        var client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Slurp.getFancyLogger().info("Requests responed with status code " + response.statusCode());
        return response;
    }

    public static HttpResponse<String> post(String path) throws IOException, InterruptedException {
        String postEndpoint = endpoint + path;

        var request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Slurp.getDrunkServer().getToken())
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        var client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Slurp.getFancyLogger().info("Requests responed with status code " + response.statusCode());
        return response;
    }

    public static HttpResponse<String> post(String path, String body) throws IOException, InterruptedException {
        String postEndpoint = endpoint + path;

        var request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Slurp.getDrunkServer().getToken())
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Slurp.getFancyLogger().info("Requests responded with status code " + response.statusCode());
        return response;
    }
}
