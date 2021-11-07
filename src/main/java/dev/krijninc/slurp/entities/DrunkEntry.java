package dev.krijninc.slurp.entities;

import com.google.gson.Gson;
import dev.krijninc.slurp.DashboardServerConnector;
import dev.krijninc.slurp.Slurp;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.UUID;

public class DrunkEntry {
    private final UUID player;
    private final int sips;
    private final int shots;

    public DrunkEntry(UUID uuid, int sips, int shots) {
        this.player = uuid;
        this.sips = sips;
        this.shots = shots;
    }

    public void save() throws IOException, InterruptedException {
        Gson gson = new Gson();

        // Notify backend of the change.
        HttpResponse<String> response = DashboardServerConnector.post("/entry", gson.toJson(this));

        // We only update the local storage if the request was successful.
        DrunkEntry returnEntry = gson.fromJson(response.body(), DrunkEntry.class);

        DrunkPlayer storedPlayer = Slurp.getDrunkPlayer(returnEntry.player);

        // Create a new player if we did't have one.
        if (storedPlayer == null) {
            storedPlayer = new DrunkPlayer(this.player);
        }

        // Update the remaining sips.
        storedPlayer.remaining.sips += returnEntry.sips;
        storedPlayer.remaining.shots += returnEntry.shots;

        // Update the total taken sips. (We add when we have subtracted from the remaining
        if (returnEntry.shots < 0) {
            storedPlayer.taken.shots =- returnEntry.shots;
        }
        if (returnEntry.sips < 0) {
            storedPlayer.taken.sips =- returnEntry.sips;
        }

        // Update the player in local storage
        Slurp.setDrunkPlayer(storedPlayer);
    }
}
