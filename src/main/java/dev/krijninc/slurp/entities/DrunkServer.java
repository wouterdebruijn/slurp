package dev.krijninc.slurp.entities;

import com.google.gson.Gson;
import dev.krijninc.slurp.DashboardServerConnector;
import dev.krijninc.slurp.Slurp;

import java.io.*;
import java.net.http.HttpResponse;
import java.util.UUID;

public class DrunkServer {
    private UUID uuid;
    private String apiToken;

    public static DrunkServer registerServer() throws IOException, InterruptedException {
        Gson gson = new Gson();
        HttpResponse<String> response = DashboardServerConnector.postNoAuth("/server");
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
    public String getApiToken() { return this.apiToken; }
}
