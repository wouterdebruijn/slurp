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

        // Create new drinking buddies from online player list.
        ArrayList<Player> playerList = new ArrayList<>(Slurp.getPlugin().getServer().getOnlinePlayers());

        // Stop if we don't have enough players
        if (playerList.size() < drinkingBuddieGroupSize) return;

        Random random = new Random();
        ArrayList<Player> drinkingBuddies = new ArrayList<>();

        for (int i = 0; i < drinkingBuddieGroupSize; i++) {
            Player player = playerList.remove(random.nextInt(playerList.size()));
            Slurp.getFancyLogger().info("Player: " + player.getDisplayName() + " has been chosen as a drinking buddy");
            drinkingBuddies.add(player);

            DrunkPlayer drunkPlayer = Slurp.getDrunkPlayer(player.getUniqueId());
            drunkPlayer.isDrinkingBuddy = true;
            Slurp.setDrunkPlayer(drunkPlayer);
        }

        // TODO: Update chat system
        Slurp.getPlugin().getServer().broadcastMessage(ChatColor.GREEN + "The new drinking buddies are: ");
        drinkingBuddies.forEach(buddy -> {
            Slurp.getPlugin().getServer().broadcastMessage(ChatColor.GREEN + buddy.getDisplayName());
        });


    }
}
