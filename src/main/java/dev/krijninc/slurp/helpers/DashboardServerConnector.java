package dev.krijninc.slurp.helpers;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.exceptions.FetchException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DashboardServerConnector {
    private static final String endpoint = ConfigLoader.getString("self-hosted-dashboard-url").equals("") ? "https://slurp.deno.dev/v1" : ConfigLoader.getString("self-hosted-dashboard-url") + "/v1";

    public static HttpResponse<String> postNoAuth(String path) throws FetchException {
        String postEndpoint = endpoint + path;

        var request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        try {
            var client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && response.statusCode() != 409) {
                reportError(response);
            }
            return response;
        } catch (IOException e) {
            Slurp.getFancyLogger().severe("Could not connect to dashboard: " + postEndpoint);
            throw new FetchException();
        } catch (InterruptedException e) {
            Slurp.getFancyLogger().severe("Connection to dashboard was interrupted! (" + endpoint + ")");
            throw new FetchException();
        }
    }

    public static HttpResponse<String> get(String path) throws FetchException {
        String postEndpoint = endpoint + path;

        var request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Slurp.getDrunkServer().getToken())
                .build();

        try {
            var client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && response.statusCode() != 409) {
                reportError(response);
            }
            return response;
        } catch (IOException e) {
            Slurp.getFancyLogger().severe("Could not connect to dashboard: " + postEndpoint);
            throw new FetchException();
        } catch (InterruptedException e) {
            Slurp.getFancyLogger().severe("Connection to dashboard was interrupted! (" + endpoint + ")");
            throw new FetchException();
        }
    }

    public static HttpResponse<String> post(String path, String body) throws FetchException {
        String postEndpoint = endpoint + path;

        var request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Slurp.getDrunkServer().getToken())
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            var client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && response.statusCode() != 409) {
                reportError(response, body);
            }
            return response;
        } catch (IOException e) {
            Slurp.getFancyLogger().severe("Could not connect to dashboard: " + postEndpoint);
            throw new FetchException();
        } catch (InterruptedException e) {
            Slurp.getFancyLogger().severe("Connection to dashboard was interrupted! (" + endpoint + ")");
            throw new FetchException();
        }
    }

    public static HttpResponse<String> postMusic(String filename) throws FetchException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://192.168.2.192:9173/sonos/192.168.2.196"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"filename\": \""+ filename +"\"}"))
                .build();

        try {
            var client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200 && response.statusCode() != 409) {
                reportError(response, filename);
            }
            return response;
        } catch (IOException e) {
            Slurp.getFancyLogger().severe("Could not connect to music.");
            throw new FetchException();
        } catch (InterruptedException e) {
            Slurp.getFancyLogger().severe("Connection to dashboard was interrupted! (" + endpoint + ")");
            throw new FetchException();
        }
    }

    private static void reportError(HttpResponse<String> response) {
        FancyLogger logger = Slurp.getFancyLogger();

        logger.severe("Request to endpoint failed!");
        logger.severe("Server responded with status code: " + response.statusCode() + ". Response body:");
        logger.info(response.body());
    }

    private static void reportError(HttpResponse<String> response, String requestBody) {
        FancyLogger logger = Slurp.getFancyLogger();

        logger.severe("Request to endpoint failed!");
        logger.severe("Server responded with status code: " + response.statusCode() + ". Response body:");
        logger.info(response.body());
        logger.info("Request body: " + requestBody);
    }
}
