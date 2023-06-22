package nl.wouterdebruijn.slurp.helper.game.handlers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import nl.wouterdebruijn.slurp.Slurp;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.function.Consumer;

public class TitleCountdownHandler {
    public static void countdown(Player player, int seconds, Consumer<Void> displayTitle) {

            Bukkit.getScheduler().runTaskAsynchronously(Slurp.plugin, () -> {
                for (int i=seconds; i > 0; i--) {
                    NamedTextColor color;

                    // change color based on seconds left of total seconds
                    if (i > seconds / 2) {
                        color = NamedTextColor.GREEN;
                    } else if (i > seconds / 4) {
                        color = NamedTextColor.YELLOW;
                    } else {
                        color = NamedTextColor.RED;
                    }

                    Title countdownTitle = Title.title(Component.text(i).color(color), Component.text(""));
                    player.showTitle(countdownTitle);
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                displayTitle.accept(null);
            });
    }
}
