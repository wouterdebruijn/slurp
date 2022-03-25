package nl.wouterdebruijn.slurp.controller;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import nl.wouterdebruijn.slurp.Slurp;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collection;

public class SmartTitleController {

    private static String getCountdownColor(int maxTime, int time) {
        if (time > (maxTime / 3) * 2) {
            return "\u00A72";
        } else if (time > (maxTime / 3)) {
            return "\u00A7e";
        } else {
            return "\u00A7c";
        }
    }

    public static void countdown(Collection<? extends Player> players, int time, String title, String subtitle, Runnable runnable) {
        for (int i = time; i > 0; i--) {
            int finalI = i;
            Bukkit.getScheduler().runTaskLater(Slurp.getPlugin(), () -> {
                for (Player player : players) {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                    player.showTitle(Title.title(Component.text(getCountdownColor(time, finalI) + finalI), Component.text("")));
                }
            }, 20L * (time - i));
        }

        Bukkit.getScheduler().runTaskLater(Slurp.getPlugin(), () -> {
            for (Player player : players) {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                player.showTitle(Title.title(Component.text("\u00A72" + title), Component.text("\u00A7a" + subtitle)));
            }
            runnable.run();
        }, 20L * time);
    }

    public static void playTitle(Player player, String title, String subtitle) {
        Bukkit.getScheduler().runTaskLater(Slurp.getPlugin(), () -> {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            player.showTitle(Title.title(Component.text(title), Component.text(subtitle)));
        }, 0);
    }
}
