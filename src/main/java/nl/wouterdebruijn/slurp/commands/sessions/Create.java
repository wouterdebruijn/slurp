package nl.wouterdebruijn.slurp.commands.sessions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpPlayer;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpSession;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpSessionManager;
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
            if (!(sender instanceof Player)) {
                sender.sendMessage(Slurp.getPrefix().append(Component.text("You must be a player to use this command!").color(NamedTextColor.RED)));
                return true;
            }

            if (SlurpPlayerManager.getPlayer((Player) sender) != null) {
                sender.sendMessage(Slurp.getPrefix().append(Component.text("You are already in a session!").color(NamedTextColor.RED)));
                return true;
            }

            Player player = (Player) sender;
            SlurpSession session = SlurpSession.create();
            sender.sendMessage(Slurp.getPrefix().append(Component.text("Created session with shortcode: ").color(NamedTextColor.GREEN).append(Component.text(session.getShortcode()).color(NamedTextColor.GOLD))));
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
