package dev.krijninc.slurp.helpers;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class SidebarManager {
    ScoreboardManager scoreboardManager;

    public SidebarManager() throws Exception {
        scoreboardManager = Slurp.getPlugin().getServer().getScoreboardManager();
        if (scoreboardManager == null) {
            throw new Exception();
        }
    }

    public void createSidebar(DrunkPlayer p) {
        Scoreboard playerBoard = scoreboardManager.getNewScoreboard();

        Objective objective = playerBoard.registerNewObjective("slurpStats", "dummy", ChatColor.GOLD + "Slurp");

        objective.getScore(ChatColor.GRAY + "===============").setScore(12);
        objective.getScore(ChatColor.GREEN + "Taken:").setScore(11);
        objective.getScore(ChatColor.GOLD + "Shots: " + p.taken.shots).setScore(10);
        objective.getScore(ChatColor.YELLOW + "Sips: " + p.taken.sips).setScore(9);
        objective.getScore("  ").setScore(8);
        objective.getScore(ChatColor.RED + "Remaining:").setScore(7);
        objective.getScore(ChatColor.GOLD + "Shots: " + p.remaining.shots).setScore(6);
        objective.getScore(ChatColor.YELLOW + "Sips: " + p.remaining.sips).setScore(5);

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Player minecraftPlayer = p.getBukkitPlayer();
        if (minecraftPlayer != null) {
            minecraftPlayer.setScoreboard(playerBoard);
            Slurp.getFancyLogger().info("Set new scoreboard for player " + minecraftPlayer.getDisplayName());
        } else {
            Slurp.getFancyLogger().warning("Could not set scoreboard for player " + p.getUuid());
        }
    }
}
