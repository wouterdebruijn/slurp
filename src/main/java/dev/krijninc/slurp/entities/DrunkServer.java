package dev.krijninc.slurp.entities;

import com.google.gson.Gson;
import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.exceptions.FetchException;
import dev.krijninc.slurp.helpers.DashboardServerConnector;

import java.io.*;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.UUID;

public class DrunkServer {
    private UUID uuid;
    private String token;
    private long seed = 80085;
    private double modifier = 1;

    public static DrunkServer registerServer() throws FetchException {
        Gson gson = new Gson();
        HttpResponse<String> response = DashboardServerConnector.postNoAuth("/server");
        DrunkServer server = gson.fromJson(response.body(), DrunkServer.class);

        // Generate new seed, we are a new Server after all.
        Random random = new Random();
        server.setSeed(random.nextLong());

        return server;
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
    public String getToken() { return this.token; }

    public Long getSeed() { return this.seed; }
    public void setSeed(long seed) {
        this.seed = seed;
    }

    public double getModifier() { return modifier; }
    public void setModifier(double modifier) { this.modifier = modifier; }
}
