package nl.wouterdebruijn.slurp.helper.game.manager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.filestorage.SlurpPlayerFileAdapter;

public class SlurpPlayerManager {
    public static HashMap<String, SlurpPlayer> players = new HashMap<>();

    public static void addPlayer(Player player, SlurpPlayer slurpPlayer) {
        players.put(player.getUniqueId().toString(), slurpPlayer);
    }

    public static SlurpPlayer getPlayer(Player player) {
        return players.get(player.getUniqueId().toString());
    }

    public static SlurpPlayer getPlayer(String id) {
        // Return the SlurpPlayer matching the given id (Slurp id)
        for (SlurpPlayer slurpPlayer : players.values()) {
            if (slurpPlayer.getId().equals(id)) {
                return slurpPlayer;
            }
        }
        return null;
    }

    public static SlurpPlayer getPlayer(Player player, SlurpSession session) {
        for (SlurpPlayer slurpPlayer : players.values()) {
            if (slurpPlayer.getPlayer().getName().equals(player.getName())
                    && slurpPlayer.getSession().getId().equals(session.getId())) {
                return slurpPlayer;
            }
        }
        return null;
    }

    /**
     * Checks if a SlurpPlayer is null and sends a message to the player if so.
     *
     * @param slurpPlayer Player where SlurpPlayer is derived from.
     * @param slurper     SlurpPlayer object to check if null
     * @return True is the SlurpPlayer is null, false if it is not.
     */
    public static boolean checkNull(Player slurpPlayer, SlurpPlayer slurper) {
        if (slurper == null) {
            slurpPlayer.sendMessage(TextBuilder.error("You are not in a session!"));
            return true;
        }
        return false;
    }

    /**
     * Checks if a SlurpPlayer is null silently.
     *
     * @param slurpPlayer Player where SlurpPlayer is derived from.
     * @param slurper     SlurpPlayer object to check if null
     * @return True is the SlurpPlayer is null, false if it is not.
     */
    public static boolean checkNullSilent(Player slurpPlayer, SlurpPlayer slurper) {
        return slurper == null;
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
                ArrayList<SlurpPlayerFileAdapter> playerList = gson.fromJson(reader,
                        new TypeToken<ArrayList<SlurpPlayerFileAdapter>>() {
                        }.getType());

                if (playerList == null) {
                    Slurp.logger.log(Level.SEVERE, "Failed to load players from disk");
                    return;
                }

                // Go through arraylist and add to hashmap
                playerList.forEach(slurpPlayerFileAdapter -> {
                    Slurp.logger.log(Level.INFO, "Loaded player: " + slurpPlayerFileAdapter.getId());
                    players.put(slurpPlayerFileAdapter.getMinecraftPlayerUuid(),
                            new SlurpPlayer(slurpPlayerFileAdapter));
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
