package nl.wouterdebruijn.slurp.repository;

import nl.wouterdebruijn.slurp.entity.SlurpPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SlurpPlayerRepository {
    private static final HashMap<UUID, SlurpPlayer> players = new HashMap<UUID, SlurpPlayer>();

    public static void put(SlurpPlayer player) {
        players.put(player.uuid, player);
    }

    public static void get(UUID uuid) {
        players.get(uuid);
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
            if (player.isDrinkingBuddy())
                drinkingBuddies.add(player);
        });

        return drinkingBuddies;
    }
}
