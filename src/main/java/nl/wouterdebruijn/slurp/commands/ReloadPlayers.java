package nl.wouterdebruijn.slurp.commands;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.controller.MessageController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ReloadPlayers implements TabExecutor {
    public ReloadPlayers() {
        Objects.requireNonNull(Slurp.getPlugin().getCommand("slurp-player-reload")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Slurp.playerReload();

        if ((sender instanceof Player player)) {
            MessageController.sendMessage(player, "Slurp players have been reloaded!");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
