package nl.wouterdebruijn.slurp.command.session;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.helper.game.minigames.RockPaperScissors;

public class PlayRockPaperScissors implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        Player player = (Player) sender;
        Player player2 = Bukkit.getPlayer(args[0]);

        if (player2 == null) {
            player.sendMessage(TextBuilder.error("Player not found!"));
            return true;
        }

        if (player2 == player) {
            player.sendMessage(TextBuilder.error("You can't play against yourself!"));
            return true;
        }

        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);
        SlurpPlayer slurpPlayer2 = SlurpPlayerManager.getPlayer(player2);

        if (slurpPlayer == null || slurpPlayer2 == null) {
            player.sendMessage(TextBuilder.error("You or the other player are not in a session!"));
            return true;
        }

        if (slurpPlayer.getSession() == null || slurpPlayer2.getSession() == null
                || !slurpPlayer.getSession().getId().equals(slurpPlayer2.getSession().getId())) {
            player.sendMessage(TextBuilder.error("You and the other player are not in the same session!"));
            return true;
        }

        RockPaperScissors rockPaperScissors = new RockPaperScissors(player, player2);
        rockPaperScissors.startRound();
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        if (args.length != 1)
            return null;

        ArrayList<String> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> players.add(player.getName()));
        return players;
    }
}