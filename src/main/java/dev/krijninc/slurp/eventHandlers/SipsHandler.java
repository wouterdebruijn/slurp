package dev.krijninc.slurp.eventHandlers;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.exceptions.FetchException;
import dev.krijninc.slurp.types.Consumables;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class SipsHandler {
    private static final Random random = new Random();

    public static ArrayList<DrunkEntry> serverSplit(ArrayList<UUID> excludedPlayers, int amountShots, int amountSips, boolean fairSplit) throws FetchException {
        HashMap<UUID, Consumables> consumablesMap = new HashMap<>();

        ArrayList<Player> participatingPlayers = getParticipatingPlayers(excludedPlayers);

        int participatingPlayerAmount = participatingPlayers.size();
        if (participatingPlayerAmount <= 0) return new ArrayList<>();

        if (fairSplit) {
            ArrayList<Player> fairSplitPlayers = new ArrayList<>(participatingPlayers);

            // For each remaining shot, we pick a player
            for (int i = 0; i < amountShots; i++) {
                if (fairSplitPlayers.size() == 0) {
                    fairSplitPlayers = new ArrayList<>(participatingPlayers);
                }

                // Remove random player from the current list.
                Player p = fairSplitPlayers.remove(fairSplitPlayers.size() > 1 ? random.nextInt(fairSplitPlayers.size()) : 0);

                // Add the shot to the player.
                Consumables consumables = consumablesMap.get(p.getUniqueId());
                if (consumables == null) consumables = new Consumables();
                consumables.shots++;
                consumablesMap.put(p.getUniqueId(), consumables);
            }

            // For each remaining sip, we pick a player
            for (int i = 0; i < amountSips; i++) {
                if (fairSplitPlayers.size() == 0) {
                    fairSplitPlayers = new ArrayList<>(participatingPlayers);
                }

                // Remove random player from the current list.
                Player p = fairSplitPlayers.remove(fairSplitPlayers.size() > 1 ? random.nextInt(fairSplitPlayers.size()) : 0);

                // Add the sip to the player.
                Consumables consumables = consumablesMap.get(p.getUniqueId());
                if (consumables == null) consumables = new Consumables();
                consumables.sips++;
                consumablesMap.put(p.getUniqueId(), consumables);
            }
        } else {
            for (int i = 0; i < amountShots; i++) {
                // Remove random player from the current list.
                Player p = participatingPlayers.get(random.nextInt(participatingPlayerAmount));

                // Add the shot to the player.
                Consumables consumables = consumablesMap.get(p.getUniqueId());
                if (consumables == null) consumables = new Consumables();
                consumables.shots++;
                consumablesMap.put(p.getUniqueId(), consumables);
            }

            for (int i = 0; i < amountSips; i++) {
                // Remove random player from the current list.
                Player p = participatingPlayers.get(random.nextInt(participatingPlayerAmount));

                // Add the sip to the player.
                Consumables consumables = consumablesMap.get(p.getUniqueId());
                if (consumables == null) consumables = new Consumables();
                consumables.sips++;
                consumablesMap.put(p.getUniqueId(), consumables);
            }
        }

        ArrayList<DrunkEntry> entries = new ArrayList<>();

        for (HashMap.Entry<UUID, Consumables> entry : consumablesMap.entrySet()) {
            DrunkEntry drunkEntry = new DrunkEntry(entry.getKey(), entry.getValue().sips, entry.getValue().shots);
            entries.add(drunkEntry.save());
        }
        return entries;
    }

    public static DrunkEntry serverNoSplit(ArrayList<UUID> excludedPlayers, int amountShots, int amountSips) throws FetchException {
        ArrayList<Player> participatingPlayers = getParticipatingPlayers(excludedPlayers);

        Player randomPlayer = participatingPlayers.get(random.nextInt(participatingPlayers.size()));
        DrunkEntry entry = new DrunkEntry(randomPlayer.getUniqueId(), amountSips, amountShots);
        entry.save();
        return entry;
    }

    public static ArrayList<DrunkEntry> everyoneDrinks(ArrayList<UUID> excludedPlayers, int amountShots, int amountSips) throws FetchException {
        ArrayList<Player> participatingPlayers = getParticipatingPlayers(excludedPlayers);

        ArrayList<DrunkEntry> entries = new ArrayList<>();

        for (Player player : participatingPlayers) {
            DrunkEntry entry = new DrunkEntry(player.getUniqueId(), amountSips, amountShots);
            entry.save();
            entries.add(entry);
        }

        return entries;
    }

    public static DrunkEntry playerDrinks(Player player, int amountShots, int amountSips) throws FetchException {
        DrunkEntry entry = new DrunkEntry(player.getUniqueId(), amountSips, amountShots);
        entry.save();
        return entry;
    }

    public static void playerGives(Player player, int amountShots, int amountSips) {
        Slurp.sendMessage(player, "Not implemented!");
    }

    private static ArrayList<Player> getParticipatingPlayers(ArrayList<UUID> excludedPlayers) {
        ArrayList<Player> participatingPlayers = new ArrayList<>();

        for (Player player : Slurp.getPlugin().getServer().getOnlinePlayers()) {
            if (!excludedPlayers.contains(player.getUniqueId())) {
                participatingPlayers.add(player);
            }
        }
        return participatingPlayers;
    }
}
