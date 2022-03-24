package nl.wouterdebruijn.slurp.serverRunnables;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.controller.ConfigController;
import nl.wouterdebruijn.slurp.controller.SidebarController;
import nl.wouterdebruijn.slurp.controller.SmartTitleController;
import nl.wouterdebruijn.slurp.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.repository.SlurpPlayerRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DrinkingBuddiesRunnable {
    public static void registerRunner() {
        Bukkit.getScheduler().runTaskTimer(Slurp.getPlugin(), DrinkingBuddiesRunnable::chooseNewDrinkingBuddies, 0, 20L * ConfigController.getInt("drinking-buddy-interval"));
    }

    private static void clearDrinkingBuddies() {
        for (SlurpPlayer player : SlurpPlayerRepository.getDrinkingBuddies()) {
            player.isDrinkingBuddy = false;
        }
    }

    private static void chooseDrinkingBuddies() {
        ArrayList<SlurpPlayer> candidates = new ArrayList<>();

        for (Player onlinePlayer : Slurp.getPlugin().getServer().getOnlinePlayers()) {
            candidates.add(SlurpPlayerRepository.get(onlinePlayer.getUniqueId()));
        }

        Random random = new Random();

        for (int i = 0; i < ConfigController.getInt("drinking-buddy-group-size"); i++) {
            int randomInt = random.nextInt(candidates.size());
            SlurpPlayer buddy = candidates.remove(randomInt);

            buddy.isDrinkingBuddy = true;
            SlurpPlayerRepository.put(buddy);
        }
    }

    public static void chooseNewDrinkingBuddies() {
        if (Slurp.getPlugin().getServer().getOnlinePlayers().size() < ConfigController.getInt("drinking-buddy-group-size")) {
            return;
        }

        clearDrinkingBuddies();
        chooseDrinkingBuddies();

        ArrayList<SlurpPlayer> buddies = SlurpPlayerRepository.getDrinkingBuddies();
        List<String> names = buddies.stream().map((slurpPlayer -> slurpPlayer.getMinecraftPlayer().getName())).toList();
        SmartTitleController.countdown(Slurp.getPlugin().getServer().getOnlinePlayers(), 3, "New Drinking buddies", String.join(" and ", names), () -> {
            for (SlurpPlayer player : SlurpPlayerRepository.get()) {
                SidebarController.createSidebar(player);
            }
        });
    }
}
