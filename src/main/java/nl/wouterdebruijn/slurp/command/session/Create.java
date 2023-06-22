package nl.wouterdebruijn.slurp.command.session;

import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Create implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(TextBuilder.error("You must be a player to execute this command."));
                return true;
            }

            if (SlurpPlayerManager.getPlayer((Player) sender) != null) {
                sender.sendMessage(TextBuilder.error("You are already in a session!"));
                return true;
            }

            SlurpSession session = SlurpSession.create();
            sender.sendMessage(TextBuilder.success(String.format("Created session %s", session.getShortcode())));
            SlurpPlayer.create(player, session.getShortcode());
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
