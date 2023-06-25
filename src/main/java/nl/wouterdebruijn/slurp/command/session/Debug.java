package nl.wouterdebruijn.slurp.command.session;

import nl.wouterdebruijn.slurp.helper.game.minigames.RockPaperScissors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Debug implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Player player2 = Bukkit.getPlayer(args[0]);

        RockPaperScissors rockPaperScissors = new RockPaperScissors(player, player2);

        rockPaperScissors.startRound();



        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
