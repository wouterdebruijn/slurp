package nl.wouterdebruijn.slurp.controller;

import net.kyori.adventure.text.Component;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.repository.SlurpPlayerRepository;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class SidebarController {
    private static final ScoreboardManager scoreboardManager = Slurp.getPlugin().getServer().getScoreboardManager();

    public static void createSidebar(SlurpPlayer player) {
        // Check if player is online
        Player minecraftPlayer = player.getMinecraftPlayer();
        if (minecraftPlayer == null) return;

        Scoreboard playerBoard = scoreboardManager.getNewScoreboard();
        Objective objective = playerBoard.registerNewObjective("slurpStats", "dummy", Component.text(ChatColor.GOLD + "Slurp"));

        objective.getScore(ChatColor.GREEN + "Taken:").setScore(20);
        objective.getScore(ChatColor.GOLD + " Shots: " + player.taken.shots).setScore(19);
        objective.getScore(ChatColor.YELLOW + " Sips: " + player.taken.sips).setScore(18);
        objective.getScore(" ").setScore(17);

        objective.getScore(ChatColor.RED + "Remaining:").setScore(16);
        objective.getScore(ChatColor.GOLD + " Shots: " + player.remaining.shots + " ").setScore(15);
        objective.getScore(ChatColor.YELLOW + " Sips: " + player.remaining.sips + " ").setScore(14);
        objective.getScore("  ").setScore(13);

        objective.getScore(ChatColor.GREEN + "Giveable:").setScore(12);
        objective.getScore(ChatColor.GOLD + " Shots: " + player.giveable.shots + "  ").setScore(11);
        objective.getScore(ChatColor.YELLOW + " Sips: " + player.giveable.sips + "  ").setScore(10);
        objective.getScore("   ").setScore(9);
        if (SlurpPlayerRepository.drinkingBuddiesCount() > 0) {
            objective.getScore(ChatColor.BLUE + "Drinking Buddies:").setScore(8);
            int scoreboardCount = 7;

            for (SlurpPlayer drinkingBuddy : SlurpPlayerRepository.getDrinkingBuddies()) {
                objective.getScore(ChatColor.AQUA + "- " + drinkingBuddy.getMinecraftPlayer().getName()).setScore(scoreboardCount--);
                if (scoreboardCount == 0) break;
            }
        }

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        minecraftPlayer.setScoreboard(playerBoard);
        LogController.info("New scoreboard created for player" + minecraftPlayer);
    }
}
