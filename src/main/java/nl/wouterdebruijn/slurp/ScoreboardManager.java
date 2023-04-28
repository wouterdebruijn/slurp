package nl.wouterdebruijn.slurp;

import net.kyori.adventure.text.Component;
import nl.wouterdebruijn.slurp.helper.game.entity.Consumable;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.infra.Observer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {
    public static void playerScoreboard(SlurpPlayer player) {
        new SlurpPlayerObserver(player);
    }

    private static class SlurpPlayerObserver implements Observer {
        SlurpPlayer player;
        Objective sidebar;
        Scoreboard scoreboard;

        BukkitRunnable hotbarTitle;
        public SlurpPlayerObserver(SlurpPlayer player) {
            this.player = player;
            this.player.attach(this);

            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            sidebar = scoreboard.registerNewObjective("sidebar", Criteria.DUMMY, Component.text("§8[§6Slurp§8]"));
            sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);

            // Register scoreboard to player
            Player minecraftPlayer = Bukkit.getPlayer(player.getUsername());
            if (minecraftPlayer == null) {
                return;
            }
            minecraftPlayer.setScoreboard(scoreboard);

            // Register observer
            player.attach(this);
        }

        @Override
        public void update() {
            Consumable remaining = player.getRemaining();
            Consumable taken = player.getTaken();
            Consumable giveable = player.getGiveable();

            // Bukkit interval
            if (hotbarTitle != null) {
                hotbarTitle.cancel();
            }

            final int[] index = {0};

            hotbarTitle = new BukkitRunnable() {
                @Override
                public void run() {
                    Player minecraftPlayer = Bukkit.getPlayer(player.getUsername());
                    if (minecraftPlayer == null) {
                        return;
                    }

                    if (index[0] < 5) {
                        minecraftPlayer.sendActionBar(Component.text("§6Remaining: §7" + remaining.toString()));
                        index[0]++;
                    }

                    else if (index[0] < 10) {
                        minecraftPlayer.sendActionBar(Component.text("§6Taken: §7" + taken.toString()));
                        index[0]++;
                    }

                    else if (index[0] < 14) {
                        minecraftPlayer.sendActionBar(Component.text("§6Giveable: §7" + giveable.toString()));
                        index[0]++;
                    }

                    else {
                        minecraftPlayer.sendActionBar(Component.text("§6Giveable: §7" + giveable.toString()));
                        index[0] = 0;
                    }
                }
            };

            hotbarTitle.runTaskTimer(Slurp.plugin, 0, 20);
        }
    }
}
