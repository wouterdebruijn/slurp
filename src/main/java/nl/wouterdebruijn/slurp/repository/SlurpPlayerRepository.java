package nl.wouterdebruijn.slurp.repository;

import com.google.gson.Gson;
import nl.wouterdebruijn.slurp.api.SlurpAPI;
import nl.wouterdebruijn.slurp.controller.LogController;
import nl.wouterdebruijn.slurp.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.exceptions.APIPostException;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SlurpPlayerRepository {
    private static final HashMap<UUID, SlurpPlayer> players = new HashMap<>();

    public static void put(SlurpPlayer player) {
        players.put(player.getUuid(), player);
    }

    public static SlurpPlayer get(UUID uuid) {
        return players.get(uuid);
    }

    public static ArrayList<SlurpPlayer> get() {
        return new ArrayList<>(players.values());
    }

    public static void remove(UUID uuid) {
        players.remove(uuid);
    }

    /**
     * Return an ArrayList of SlurpPlayers that are drinking buddies.
     */
    public static ArrayList<SlurpPlayer> getDrinkingBuddies() {
        ArrayList<SlurpPlayer> drinkingBuddies = new ArrayList<>();

        players.forEach((uuid, player) -> {
            if (player.isDrinkingBuddy)
                drinkingBuddies.add(player);
        });

        return drinkingBuddies;
    }

    /**
     * Return the amount of active drinking buddies.
     */
    public static int drinkingBuddiesCount() {
        ArrayList<SlurpPlayer> drinkingBuddies = new ArrayList<>();

        players.forEach((uuid, player) -> {
            if (player.isDrinkingBuddy)
                drinkingBuddies.add(player);
        });

        return drinkingBuddies.size();
    }

    public static SlurpPlayer register(SlurpPlayer player) throws APIPostException {
        try {
            HttpResponse<String> response = SlurpAPI.post("/player", player);
            Gson gson = new Gson();

            if (response.statusCode() == 200) {
                // We have created a new player return this.
                LogController.info("New player " + player.getUuid() + " was added to the dashboard!");
                return gson.fromJson(response.body(), SlurpPlayer.class);
            }

            LogController.info("Player " + player.getUuid() + " exists on dashboard, caching data.");
            HttpResponse<String> existingPlayerJSON = SlurpAPI.get("/player/" + player.getUuid());
            return gson.fromJson(existingPlayerJSON.body(), SlurpPlayer.class);
        } catch (Exception e) {
            LogController.error("Could not register new player");
            e.printStackTrace();
            throw new APIPostException();
        }
    }
}
