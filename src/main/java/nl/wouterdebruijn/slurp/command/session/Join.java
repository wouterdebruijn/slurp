package nl.wouterdebruijn.slurp.command.session;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpSessionManager;
import nl.wouterdebruijn.slurp.exceptions.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Join implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!(sender instanceof Player)) {
                sender.sendMessage(TextBuilder.error("You must be a player to execute this command!"));
                return true;
            }

            if (SlurpPlayerManager.getPlayer((Player) sender) != null) {
                sender.sendMessage(TextBuilder.error("You are already in a session!"));
                return true;
            }

            Player player = (Player) sender;

            try {
                String shortcode = String.join(" ", args);
                SlurpPlayer slurpPlayer = SlurpPlayer.create(player, shortcode);

                player.sendMessage(TextBuilder.success(String.format("Joined session %s", slurpPlayer.getSession().getUuid())));

                Slurp.logger.log(Level.INFO, String.format("Player %s joined session %s", player.getName(), slurpPlayer.getSession().getUuid()));
            } catch (ApiResponseException e) {
                player.sendMessage(TextBuilder.error(e.getMessage()));
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 1) {
            return new ArrayList<>();
        }

        ArrayList<SlurpSession> sessions = SlurpSessionManager.dump();

        ArrayList<String> sessionShortcodes = new ArrayList<>();
        for (SlurpSession session : sessions) {
            sessionShortcodes.add(session.getShortcode());
        }

        return sessionShortcodes;
    }
}