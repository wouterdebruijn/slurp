package dev.krijninc.slurp.entities;

import com.google.gson.Gson;
import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.exceptions.FetchException;
import dev.krijninc.slurp.helpers.ConfigLoader;
import dev.krijninc.slurp.helpers.DashboardServerConnector;

import java.net.http.HttpResponse;
import java.util.UUID;

public class DrunkEntry {
    private final UUID player;
    private final int sips;
    private final int shots;
    private final boolean buddyEntry;

    public DrunkEntry(UUID uuid, double sips, double shots) {
        this.player = uuid;
        buddyEntry = false;

        if (shots % 1.0 > 0) {
            this.sips = (int) (shots * ConfigLoader.getInt("shots-to-sips-multiplier") + sips);
        } else {
            this.sips = (int) sips;
        }
        this.shots = (int) shots;
    }

    public DrunkEntry(UUID uuid, double sips, double shots, boolean buddyEntry) {
        this.player = uuid;
        this.buddyEntry = buddyEntry;

        if (shots % 1.0 > 0) {
            this.sips = (int) (shots * ConfigLoader.getInt("shots-to-sips-multiplier") + sips);
        } else {
            this.sips = (int) sips;
        }
        this.shots = (int) shots;
    }

    public DrunkEntry save() throws FetchException {
        Gson gson = new Gson();
        // Notify backend of the change.
        HttpResponse<String> response = DashboardServerConnector.post("/entry", gson.toJson(this));

        // We only update the local storage if the request was successful.
        DrunkEntry returnEntry = gson.fromJson(response.body(), DrunkEntry.class);

        DrunkPlayer storedPlayer = Slurp.getDrunkPlayer(returnEntry.player);

        // Create a new player if we didn't have one.
        if (storedPlayer == null) {
            storedPlayer = new DrunkPlayer(this.player);
        }

        // Update the remaining sips.
        storedPlayer.remaining.sips += returnEntry.sips;
        storedPlayer.remaining.shots += returnEntry.shots;

        // Update the total taken sips. (We add when we have subtracted from the remaining
        if (returnEntry.shots < 0) {
            storedPlayer.taken.shots -= returnEntry.shots;
        }
        if (returnEntry.sips < 0) {
            storedPlayer.taken.sips -= returnEntry.sips;
        }

        // Update the player in local storage
        Slurp.setDrunkPlayer(storedPlayer);
        return returnEntry;
    }

    public int getSips() {
        return sips;
    }

    public int getShots() {
        return shots;
    }

    public UUID getPlayer() {
        return player;
    }

    public boolean isBuddyEntry() {
        return buddyEntry;
    }
}
