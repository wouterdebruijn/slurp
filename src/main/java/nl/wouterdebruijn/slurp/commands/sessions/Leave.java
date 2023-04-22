package nl.wouterdebruijn.slurp.commands.sessions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpPlayer;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpSession;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpSessionManager;
import nl.wouterdebruijn.slurp.helpers.slurp.exceptions.ApiResponseException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Leave implements TabExecutor {

    private boolean hasRemainingPlayers(SlurpSession session) {
        for (SlurpPlayer slurpPlayerI : SlurpPlayerManager.dump()) {
            if (slurpPlayerI.getSession().getUuid().equals(session.getUuid())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (slurpPlayer == null) {
            player.sendMessage(Slurp.getPrefix().append(Component.text("You are not in a session", NamedTextColor.RED)));
            return true;
        }

        SlurpSession session = SlurpSessionManager.getSession(slurpPlayer.getSession().getUuid());

        if (session == null) {
            player.sendMessage(Slurp.getPrefix().append(Component.text("You are not in a session?", NamedTextColor.RED)));
            return true;
        }

        SlurpPlayerManager.remove(player);
        player.sendMessage(Slurp.getPrefix().append(Component.text("You have left the session.", NamedTextColor.GREEN)));

        if (!hasRemainingPlayers(session)) {
            SlurpSessionManager.remove(session);
            Slurp.logger.log(Level.INFO, "Removed session " + session.getUuid() + " because it has no remaining players.");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
