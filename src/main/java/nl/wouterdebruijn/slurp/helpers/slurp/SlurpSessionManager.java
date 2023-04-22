package nl.wouterdebruijn.slurp.helpers.slurp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import nl.wouterdebruijn.slurp.Slurp;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;

public class SlurpSessionManager {
    public static ArrayList<SlurpSession> sessions = new ArrayList<>();

    public static SlurpSession getSession(String session) {
        for (SlurpSession slurpSession : sessions) {
            if (slurpSession.getUuid().equals(session)) {
                return slurpSession;
            }
        }
        return null;
    }

    public static ArrayList<SlurpSession> dump() {
        return sessions;
    }

    public static void saveToDisk() {
        try {
            Gson gson = new Gson();
            File file = new File(Slurp.plugin.getDataFolder().getAbsolutePath(), "sessions.json");
            file.getParentFile().mkdir();

            file.createNewFile();

            Writer writer = new FileWriter(file, false);
            gson.toJson(sessions, writer);

            writer.flush();
            writer.close();
            Slurp.logger.info("Saved sessions to disk");
        } catch (Exception e) {
            Slurp.logger.log(Level.SEVERE, "Failed to save sessions to disk");
            throw new RuntimeException(e);
        }
    }

    public static void loadFromDisk() {
        try {
            Gson gson = new Gson();
            File file = new File(Slurp.plugin.getDataFolder().getAbsolutePath(), "sessions.json");
            if (file.exists()) {
                Reader reader = new FileReader(file);

                // Restore the player list
                sessions = gson.fromJson(reader, new TypeToken<ArrayList<SlurpSession>>() {}.getType());

                // Dump sessions
                sessions.forEach(slurpSession -> {
                    Slurp.logger.info("Loaded session: " + slurpSession.getUuid());
                });

                Slurp.logger.info("Loaded sessions from disk");

                reader.close();
            }
        } catch (Exception e) {
            Slurp.logger.log(Level.SEVERE, "Failed to load sessions from disk");
            throw new RuntimeException(e);
        }
    }

    public static void addSession(SlurpSession session) {
        sessions.add(session);
    }
}
