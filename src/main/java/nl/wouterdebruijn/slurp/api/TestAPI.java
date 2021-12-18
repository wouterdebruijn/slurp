package nl.wouterdebruijn.slurp.api;

import nl.wouterdebruijn.slurp.controller.LogController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestAPI {
    public static HttpResponse<String> get() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 && response.statusCode() != 409) {
            reportError(response);
        }
        return response;
    }

    private static void reportError(HttpResponse<String> response) {
        LogController.error("Request to endpoint failed!");
        LogController.error("Server responded with status code: " + response.statusCode());

        LogController.debug(String.format("URI: %s\nServer responded with body: '%s'", response.uri(), response.body()));
    }

    private static void reportError(HttpResponse<String> response, String requestBody) {
        reportError(response);
        LogController.debug(String.format("Requested Body: '%s'", requestBody));
    }
}
