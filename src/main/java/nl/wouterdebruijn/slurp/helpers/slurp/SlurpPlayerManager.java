package nl.wouterdebruijn.slurp.helpers.slurp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import nl.wouterdebruijn.slurp.Slurp;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class SlurpPlayerManager {

    public static HashMap<String, SlurpPlayer> players = new HashMap<>();

    public static void addPlayer(Player player, SlurpPlayer slurpPlayer) {
        players.put(player.getUniqueId().toString(), slurpPlayer);
    }

    public static SlurpPlayer getPlayer(Player player) {
        return players.get(player.getUniqueId().toString());
    }

    public static ArrayList<SlurpPlayer> dump() {
        return new ArrayList<>(players.values());
    }

    public static void loadFromDisk() {
        try {
            Gson gson = new Gson();
            File file = new File(Slurp.plugin.getDataFolder().getAbsolutePath(), "players.json");
            if (file.exists()) {
                Reader reader = new FileReader(file);

                // Restore the player list
                ArrayList<SlurpPlayerGson> playerList = gson.fromJson(reader, new TypeToken<ArrayList<SlurpPlayerGson>>() {}.getType());

                if (playerList == null) {
                    Slurp.logger.log(Level.SEVERE, "Failed to load players from disk");
                    return;
                }

                // Go through arraylist and add to hashmap
                playerList.forEach(slurpPlayerGson -> {
                    Slurp.logger.log(Level.INFO, "Loaded player: " + slurpPlayerGson.getUsername());
                    players.put(slurpPlayerGson.getMinecraftPlayerUuid(), new SlurpPlayer(slurpPlayerGson));
                });

                Slurp.logger.log(Level.INFO, "Loaded players from disk");
            }
        } catch (Exception e) {
            Slurp.logger.log(Level.SEVERE, "Failed to load players from disk");
            throw new RuntimeException(e);
        }
    }

    public static void saveToDisk() {
        try {
            Gson gson = new Gson();
            File file = new File(Slurp.plugin.getDataFolder().getAbsolutePath(), "players.json");
            file.getParentFile().mkdir();
            file.createNewFile();
            Writer writer = new FileWriter(file, false);

            ArrayList<SlurpPlayerGson> playerList = new ArrayList<>();

            // Go through hashmap and add to arraylist
            players.forEach((minecraftUuid, slurpPlayer) -> {
                playerList.add(new SlurpPlayerGson(slurpPlayer, minecraftUuid));
            });

            gson.toJson(playerList, writer);

            writer.flush();
            writer.close();
            Slurp.logger.log(Level.INFO, "Saved players to disk");
        } catch (IOException e) {
            Slurp.logger.log(Level.SEVERE, "Could not save players to disk");
            e.printStackTrace();
        }
    }
}
