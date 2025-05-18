package nl.wouterdebruijn.slurp.helper.game.entity;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.endpoint.PocketBase;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.manager.DrinkingBuddyManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;

public class SlurpEntry {
    private static final String API_URL = SlurpConfig.apiUrl();
    private final String id;
    private final int units;
    private final SlurpPlayer player;
    private final boolean giveable;
    private final boolean transfer;

    public SlurpEntry(String id, int units, SlurpPlayer player, boolean giveable, boolean transfer) {
        this.id = id;
        this.units = units;
        this.player = player;
        this.giveable = giveable;
        this.transfer = transfer;
    }

    /**
     * Create a new entry in the API not accounting for drinking buddies
     * 
     * @param entry The entry to create
     * @param token The token to use for authentication
     * @return A completable future with the created entry
     */
    public static CompletableFuture<SlurpEntry> createDirect(SlurpEntryBuilder entry) {
        // Wishfully update the player's balances, faults will be corrected by the
        // websocket listener
        SlurpPlayer player = SlurpPlayerManager.getPlayer(entry.getPlayerId());

        if (player == null) {
            return CompletableFuture.failedFuture(new Exception("Player not found"));
        }

        player.setTaken(player.getTaken() + entry.getTaken());
        player.setGiveable(player.getGiveable() + entry.getGiveable());
        player.setRemaining(player.getRemaining() + entry.getRemaining());

        return PocketBase.createEntry(entry);
    }

    /**
     * Create an entry for the player and all of their drinking buddies
     * 
     * @param entry The entry to create
     * @param token The token to use for authentication
     * @return A completable future containing the created entries
     */
    public static CompletableFuture<ArrayList<SlurpEntry>> create(SlurpEntryBuilder entry) {

        // Get drinking buddies for player
        SlurpPlayer player = SlurpPlayerManager.getPlayer(entry.getPlayerId());

        if (player == null) {
            Slurp.logger.log(Level.SEVERE, "Player not found");
            return CompletableFuture.failedFuture(new Exception("Player not found"));
        }

        // Get list of drinkingbuddies
        ArrayList<SlurpPlayer> buddies = DrinkingBuddyManager.getBuddiesOfPlayer(player);

        ArrayList<SlurpEntry> entries = new ArrayList<>();

        try {

            // Create entry for player
            entries.add(createDirect(entry).get());

            // If the player has buddies create entries for them too
            if (buddies != null && entry.shouldTransferToDrinkingBuddies()) {
                // Prepare futures
                CompletableFuture<SlurpEntry>[] futures = new CompletableFuture[buddies.size()];
                for (SlurpPlayer buddy : buddies) {
                    futures[buddies.indexOf(buddy)] = createDirect(entry.copyForPlayer(buddy));
                }

                // Wait for all drinking buddies entries to be posted.
                CompletableFuture.allOf(futures).join();

                // Add all entries to the list
                for (CompletableFuture<SlurpEntry> future : futures) {
                    entries.add(future.get());
                }
            }

            return CompletableFuture.completedFuture(entries);
        } catch (Exception e) {
            Slurp.logger.log(Level.SEVERE, "Error while creating entry", e);
            return CompletableFuture.failedFuture(e);
        }
    }
}
