package nl.wouterdebruijn.slurp.command.session;

import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpSessionManager;

public class Leave implements TabExecutor {

    private boolean hasRemainingPlayers(SlurpSession session) {
        for (SlurpPlayer slurpPlayerI : SlurpPlayerManager.dump()) {
            if (slurpPlayerI.getSession().getId().equals(session.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        Player player = (Player) sender;
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (slurpPlayer == null) {
            player.sendMessage(TextBuilder.error("You are not in a session."));
            return true;
        }

        SlurpSession session = SlurpSessionManager.getSession(slurpPlayer.getSession().getId());

        if (session == null) {
            player.sendMessage(TextBuilder.error("You are not in a session."));
            return true;
        }

        SlurpPlayerManager.remove(player);
        player.sendMessage(TextBuilder.success("You have left the session."));

        if (!hasRemainingPlayers(session)) {
            SlurpSessionManager.removeSession(session);
            Bukkit.broadcast(TextBuilder.info(
                    String.format("Player %s left session %s", player.getName(), slurpPlayer.getSession().getId())),
                    "slurp.drinker");
            Slurp.logger.log(Level.INFO,
                    "Removed session " + session.getId() + " because it has no remaining players.");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
