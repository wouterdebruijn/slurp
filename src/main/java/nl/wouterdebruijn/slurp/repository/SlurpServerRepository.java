package nl.wouterdebruijn.slurp.repository;

import com.google.gson.Gson;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.api.SlurpAPI;
import nl.wouterdebruijn.slurp.controller.LogController;
import nl.wouterdebruijn.slurp.entity.SlurpServer;
import nl.wouterdebruijn.slurp.exceptions.APIPostException;

import java.io.*;
import java.net.http.HttpResponse;

public class SlurpServerRepository {
    private static SlurpServer server;

    public static void saveToJSON() {
        try {
            Gson gson = new Gson();
            File file = new File(Slurp.getPlugin().getDataFolder().getAbsolutePath() + "/server.json");
            file.getParentFile().mkdir();
            file.createNewFile();
            Writer writer = new FileWriter(file, false);

            gson.toJson(server, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            LogController.error("Could not save SlurpServer object to server.json file.");
            e.printStackTrace();
        }
    }

    public static SlurpServer loadFromJSON() throws FileNotFoundException {
        Gson gson = new Gson();
        File file = new File(Slurp.getPlugin().getDataFolder().getAbsolutePath() + "/server.json");
        if (file.exists()) {
            Reader reader = new FileReader(file);
            server = gson.fromJson(reader, SlurpServer.class);
            return server;
        }
        return null;
    }

    public static SlurpServer registerServer() throws APIPostException {
        try {
            Gson gson = new Gson();
            HttpResponse<String> response = SlurpAPI.post("server", "{}");

            server = gson.fromJson(response.body(), SlurpServer.class);
            return server;
        } catch (Exception e) {
            LogController.error("Could not register server on dashboard.");
            throw new APIPostException();
        }
    }

    public static SlurpServer get() {
        return server;
    }

    public static void set(SlurpServer newServer) {
        server = newServer;
    }
}
