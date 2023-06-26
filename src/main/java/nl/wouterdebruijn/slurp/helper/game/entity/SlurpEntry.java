package nl.wouterdebruijn.slurp.helper.game.entity;

import com.google.gson.Gson;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.exceptions.ApiResponseException;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.api.ResponseEntry;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.manager.DrinkingBuddyManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class SlurpEntry {
    private static final String API_URL = SlurpConfig.apiUrl();
    private String uuid;
    private int sips;
    private int shots;
    private SlurpPlayer player;
    private boolean giveable;
    private boolean transfer;

    public SlurpEntry(String uuid, int sips, int shots, SlurpPlayer player, boolean giveable, boolean transfer) {
        this.uuid = uuid;
        this.sips = sips;
        this.shots = shots;
        this.player = player;
        this.giveable = giveable;
        this.transfer = transfer;
    }

    public static CompletableFuture<SlurpEntry> createDirect(SlurpEntryBuilder entry, String token) {


        try {
            Gson gson = new Gson();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL + "/entry"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(entry)))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new ApiResponseException(request, response);
            }

            ResponseEntry responseEntry = gson.fromJson(response.body(), ResponseEntry.class);
            return CompletableFuture.completedFuture(responseEntry.toSlurpEntry());
        } catch (Exception e) {
            Slurp.logger.log(Level.SEVERE, "Error while creating entry", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    public static CompletableFuture<ArrayList<SlurpEntry>> create(SlurpEntryBuilder entry, String token) {

        // Get drinking buddies for player
        SlurpPlayer player = SlurpPlayerManager.getPlayer(entry.getPlayerUuid());

        if (player == null) {
            Slurp.logger.log(Level.SEVERE, "Player not found");
            return CompletableFuture.failedFuture(new Exception("Player not found"));
        }

        // Get list of drinkingbuddies
        ArrayList<SlurpPlayer> buddies = DrinkingBuddyManager.getBuddiesOfPlayer(player);

        ArrayList<SlurpEntry> entries = new ArrayList<>();

        try {

            // Create entry for player
            entries.add(createDirect(entry, token).get());

            // If the player has buddies create entries for them too
            if (buddies != null && entry.shouldTransferToDrinkingBuddies()) {
                // Prepare futures
                CompletableFuture[] futures = new CompletableFuture[buddies.size()];
                for (SlurpPlayer buddy : buddies) {
                    futures[buddies.indexOf(buddy)] = createDirect(entry, token);
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
