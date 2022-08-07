package nl.wouterdebruijn.slurp.api;

import com.google.gson.Gson;
import nl.wouterdebruijn.slurp.controller.ConfigController;
import nl.wouterdebruijn.slurp.controller.LogController;
import nl.wouterdebruijn.slurp.exceptions.APIPostException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SlurpAPI {
    public static HttpResponse<String> post(String path, Object body) throws IOException, InterruptedException, APIPostException {
        String postEndpoint = getEndpoint() + path;
        Gson gson = new Gson();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 && response.statusCode() != 409) {
            reportError(response, gson.toJson(body));
            throw new APIPostException();
        }

        LogController.debug("API POST Request (" + postEndpoint + ")" + gson.toJson(body));
        LogController.debug("API POST Response: " + response.body());

        return response;
    }

    public static HttpResponse<String> get(String path) throws IOException, InterruptedException {
        String postEndpoint = getEndpoint() + path;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 && response.statusCode() != 409) {
            reportError(response);
        }

        LogController.debug("API GET Request (" + postEndpoint + ")");
        LogController.debug("API GET Response: " + response.body());

        return response;
    }

    private static String getEndpoint() {
        String configUrl = ConfigController.getString("self-hosted-dashboard-url");
        return configUrl.equals("") ? "https://slurp.deno.dev/v1" : configUrl + "/v1";
    }

    private static void reportError(HttpResponse<String> response) {
        LogController.error("Request to endpoint failed!");
        LogController.error("Server responded with status code: " + response.statusCode());
    }

    private static void reportError(HttpResponse<String> response, String requestBody) {
        reportError(response);
    }
}
