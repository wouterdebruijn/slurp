package nl.wouterdebruijn.slurp.helper.game.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.filestorage.SlurpPlayerFileAdapter;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class SlurpPlayerManager {
    public static HashMap<String, SlurpPlayer> players = new HashMap<>();

    public static void addPlayer(Player player, SlurpPlayer slurpPlayer) {
        players.put(player.getUniqueId().toString(), slurpPlayer);
    }

    public static SlurpPlayer getPlayer(Player player) {
        return players.get(player.getUniqueId().toString());
    }

    public static SlurpPlayer getPlayer(String uuid) {
//        Return the SlurpPlayer matching the given UUID (Slurp UUID)
        for (SlurpPlayer slurpPlayer : players.values()) {
            if (slurpPlayer.getUuid().equals(uuid)) {
                return slurpPlayer;
            }
        }
        return null;
    }

    public static SlurpPlayer getPlayer(Player player, SlurpSession session) {
        for (SlurpPlayer slurpPlayer : players.values()) {
            if (slurpPlayer.getUsername().equals(player.getName()) && slurpPlayer.getSession().getUuid().equals(session.getUuid())) {
                return slurpPlayer;
            }
        }
        return null;
    }


    public static ArrayList<SlurpPlayer> dump() {
        return new ArrayList<>(players.values());
    }

    public static void remove(Player player) {
        SlurpPlayer slurpPlayer = players.get(player.getUniqueId().toString());
        slurpPlayer.detachAll();
        players.remove(player.getUniqueId().toString());
    }

    public static void loadFromDisk() {
        try {
            Gson gson = new Gson();
            File file = new File(Slurp.plugin.getDataFolder().getAbsolutePath(), "players.json");
            if (file.exists()) {
                Reader reader = new FileReader(file);

                // Restore the player list
                ArrayList<SlurpPlayerFileAdapter> playerList = gson.fromJson(reader, new TypeToken<ArrayList<SlurpPlayerFileAdapter>>() {
                }.getType());

                if (playerList == null) {
                    Slurp.logger.log(Level.SEVERE, "Failed to load players from disk");
                    return;
                }

                // Go through arraylist and add to hashmap
                playerList.forEach(slurpPlayerFileAdapter -> {
                    Slurp.logger.log(Level.INFO, "Loaded player: " + slurpPlayerFileAdapter.getUsername());
                    players.put(slurpPlayerFileAdapter.getMinecraftPlayerUuid(), new SlurpPlayer(slurpPlayerFileAdapter));
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

            ArrayList<SlurpPlayerFileAdapter> playerList = new ArrayList<>();

            // Go through hashmap and add to arraylist
            players.forEach((minecraftUuid, slurpPlayer) -> {
                playerList.add(new SlurpPlayerFileAdapter(slurpPlayer, minecraftUuid));
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
