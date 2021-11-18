package dev.krijninc.slurp.runnables;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class ChooseDrinkingBuddies extends BukkitRunnable {

    private final int drinkingBuddieGroupSize;

    private final ArrayList<Player> drinkingBuddies = new ArrayList<>();

    public ChooseDrinkingBuddies(int groupSize) {
        this.drinkingBuddieGroupSize = groupSize;
    }

    @Override
    public void run() {
        HashMap<UUID, DrunkPlayer> registeredPlayers = Slurp.getDrunkPlayers();

        // Clear all old drinking buddies
        registeredPlayers.forEach((_uuid, drunkPlayer) -> {
            drunkPlayer.isDrinkingBuddy = false;
            Slurp.setDrunkPlayer(drunkPlayer);
        });

        // Clear the local array;
        drinkingBuddies.clear();

        // Create new drinking buddies from online player list.
        ArrayList<Player> playerList = new ArrayList<>(Slurp.getPlugin().getServer().getOnlinePlayers());

        // Stop if we don't have enough players
        if (playerList.size() < drinkingBuddieGroupSize) return;

        Random random = new Random();

        for (int i = 0; i < drinkingBuddieGroupSize; i++) {
            Player player = playerList.remove(random.nextInt(playerList.size()));
            Slurp.getFancyLogger().info("Player: " + player.getDisplayName() + " has been chosen as a drinking buddy");
            drinkingBuddies.add(player);
        }

        // Only write the data after we have chosen all the buddies. Sidebar wouldn't be able to display the complete drinking buddy list.
        for (Player player : drinkingBuddies) {
            DrunkPlayer drunkPlayer = Slurp.getDrunkPlayer(player.getUniqueId());
            drunkPlayer.isDrinkingBuddy = true;
            Slurp.setDrunkPlayer(drunkPlayer);
        }

        Slurp.broadcastMessage(ChatColor.GREEN + "The new drinking buddies have been chosen!");
    }

    public ArrayList<Player> getDrinkingBuddies() {
        return drinkingBuddies;
    }
}
