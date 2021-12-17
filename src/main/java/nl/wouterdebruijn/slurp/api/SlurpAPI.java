package nl.wouterdebruijn.slurp.api;

import nl.wouterdebruijn.slurp.controller.ConfigController;
import nl.wouterdebruijn.slurp.controller.LogController;
import nl.wouterdebruijn.slurp.repository.SlurpServerRepository;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SlurpAPI {
    public static HttpResponse<String> post(String path, String body) throws IOException, InterruptedException {
        String postEndpoint = getEndpoint();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(postEndpoint))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + SlurpServerRepository.get().bearerToken)
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 && response.statusCode() != 409) {
            reportError(response, body);
        }
        return response;
    }

    private static String getEndpoint() {
        String configUrl = ConfigController.getString("self-hosted-dashboard-url");
        return configUrl.equals("") ? "https://slurp.deno.dev/v1" : configUrl + "/v1";
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
