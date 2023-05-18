package nl.wouterdebruijn.slurp.helper.game.manager;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.Consumable;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.infra.Observer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ScoreboardManager {
    public static void playerScoreboard(SlurpPlayer player) {
        new SlurpPlayerObserver(player);
    }

    private static class SlurpPlayerObserver implements Observer {
        // Create line arraylist with default values
        private final List<String> lines = new ArrayList<>() {{
            add("§cRemaining");
            add("§7Sips: §f00");
            add("§7Shots: §f00");
            add("");
            add("§aTaken");
            add("§r§7Sips: §f00");
            add("§r§7Shots: §f00");
            add(" ");
            add("§9Giveable");
            add("§r§r§7Sips: §f00");
            add("§r§r§7Shots: §f00");
            add("  ");
            add("§aDrinking buddies:");
        }};
        private final List<String> oldLines = new ArrayList<>();
        SlurpPlayer player;
        Objective sidebar;
        Scoreboard scoreboard;

        public SlurpPlayerObserver(SlurpPlayer player) {
            this.player = player;
            this.player.attach(this);

            oldLines.addAll(lines);

            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            sidebar = scoreboard.registerNewObjective("sidebar", Criteria.DUMMY, TextBuilder.prefix());
            sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);

            // Register scoreboard to player
            Player minecraftPlayer = Bukkit.getPlayer(player.getUsername());
            if (minecraftPlayer == null) {
                Slurp.logger.warning("Could not find player " + player.getUsername() + " in Bukkit");
                return;
            }

            minecraftPlayer.setScoreboard(scoreboard);

            // Register observer
            player.attach(this);

            Slurp.logger.info("Registered scoreboard for player " + player.getUsername());

            // Update scoreboard
            update();
        }

        private void setRemaining(int sips, int shots) {
            lines.set(1, "§7Sips: §f" + sips);
            lines.set(2, "§7Shots: §f" + shots);
        }

        private void setTaken(int sips, int shots) {
            lines.set(5, "§r§7Sips: §f" + sips);
            lines.set(6, "§r§7Shots: §f" + shots);
        }

        private void setGiveable(int sips, int shots) {
            lines.set(9, "§r§r§7Sips: §f" + sips);
            lines.set(10, "§r§r§7Shots: §f" + shots);
        }

        private boolean hasChanged() {
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String oldLine = oldLines.get(i);

                if (!line.equals(oldLine)) {
                    return true;
                }
            }

            return false;
        }

        private void updateSidebar() {
            if (!hasChanged()) {
                return;
            }

            // Update sidebar
            for (int i = 0; i < 13; i++) {
                String line = lines.get(i);
                String oldLine = oldLines.get(i);

                scoreboard.resetScores(oldLine);
                sidebar.getScore(line).setScore(99 - i);

                oldLines.set(i, line);
            }

            ArrayList<SlurpPlayer> players = DrinkingBuddyManager.getDrinkingBuddies(player.getSession());

            for (var pl : players) {
                Slurp.logger.info("---- " + pl.getUsername());
            }

            for (int i = 13; i < 13 + players.size(); i++) {
                if (oldLines.size() >= i) {
                    oldLines.add(null);
                }

                String oldLine = oldLines.get(i);

                if (oldLine != null) {
                    scoreboard.resetScores(oldLine);
                }

                String line = players.get(i - 13).getUsername();

                if (lines.size() >= i) {
                    lines.add(null);
                }

                lines.set(i, line);

                sidebar.getScore(line).setScore(80 - i);
                oldLines.set(i, line);
            }
        }

        @Override
        public void update() {
            Consumable remaining = player.getRemaining();
            Consumable taken = player.getTaken();
            Consumable giveable = player.getGiveable();

            setRemaining(remaining.getSips(), remaining.getShots());
            setTaken(taken.getSips(), taken.getShots());
            setGiveable(giveable.getSips(), giveable.getShots());

            updateSidebar();
        }

        @Override
        public void destroy() {
            player.detach(this);

            Player minecraftPlayer = Bukkit.getPlayer(player.getUsername());
            if (minecraftPlayer == null) {
                Slurp.logger.warning("Could not find player " + player.getUsername() + " in Bukkit");
                return;
            }

            minecraftPlayer.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }
}
