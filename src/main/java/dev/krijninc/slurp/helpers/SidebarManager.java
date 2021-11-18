package dev.krijninc.slurp.helpers;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class SidebarManager {
    ScoreboardManager scoreboardManager;

    public SidebarManager() throws Exception {
        scoreboardManager = Slurp.getPlugin().getServer().getScoreboardManager();
        if (scoreboardManager == null) {
            throw new Exception();
        }
    }

    public void createSidebar(DrunkPlayer p) {
        // Check if player is online
        Player minecraftPlayer = p.getBukkitPlayer();
        if (minecraftPlayer == null) return;

        Scoreboard playerBoard = scoreboardManager.getNewScoreboard();

        Objective objective = playerBoard.registerNewObjective("slurpStats", "dummy", ChatColor.GOLD + "Slurp");

        objective.getScore(ChatColor.GRAY + "===============").setScore(15);
        objective.getScore(ChatColor.GREEN + "Taken:").setScore(14);
        objective.getScore(ChatColor.GOLD + " Shots: " + p.taken.shots).setScore(13);
        objective.getScore(ChatColor.YELLOW + " Sips: " + p.taken.sips).setScore(12);
        objective.getScore("  ").setScore(11);
        objective.getScore(ChatColor.RED + "Remaining:").setScore(10);
        objective.getScore(ChatColor.GOLD + " Shots: " + p.remaining.shots).setScore(9);
        objective.getScore(ChatColor.YELLOW + " Sips: " + p.remaining.sips).setScore(8);
        objective.getScore("   ").setScore(7);
        if (p.isDrinkingBuddy) {
            objective.getScore(ChatColor.DARK_GRAY + "Drinking Buddy:").setScore(6);
            int scoreboardCount = 2;

            for (Player player : Slurp.getDrinkingBuddies()) {
                objective.getScore("-" + player.getDisplayName()).setScore(scoreboardCount--);
                if (scoreboardCount == 0) break;
            }
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        minecraftPlayer.setScoreboard(playerBoard);
        Slurp.getFancyLogger().info("Set new scoreboard for player " + minecraftPlayer.getDisplayName());
    }
}
