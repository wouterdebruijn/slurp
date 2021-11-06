package dev.krijninc.slurp.entities;

import com.google.gson.Gson;
import dev.krijninc.slurp.Slurp;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class DrunkServer {
    private UUID uuid;
    private String apiToken;

    public static DrunkServer registerServer() throws IOException, InterruptedException {
        Gson gson = new Gson();
        String postEndpoint = "https://slurp.deno.dev/v1/server";

        var request = HttpRequest.newBuilder()
                .uri(URI.create(postEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{}"))
                .build();

        var client = HttpClient.newHttpClient();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        return gson.fromJson(response.body(), DrunkServer.class);
    }

    public void save() throws IOException {
        Gson gson = new Gson();
        File file = new File(Slurp.getPlugin().getDataFolder().getAbsolutePath() + "/server.json");
        file.getParentFile().mkdir();
        file.createNewFile();
        Writer writer = new FileWriter(file, false);
        gson.toJson(this, writer);
        writer.flush();
        writer.close();
    }

    public static DrunkServer loadServer() throws FileNotFoundException {
        Gson gson = new Gson();
        File file = new File(Slurp.getPlugin().getDataFolder().getAbsolutePath() + "/server.json");
        if (file.exists()) {
            Reader reader = new FileReader(file);
            return gson.fromJson(reader, DrunkServer.class);
        }
        return null;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}
