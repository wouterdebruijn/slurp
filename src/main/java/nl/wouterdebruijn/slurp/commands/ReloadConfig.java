package nl.wouterdebruijn.slurp.commands;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.controller.MessageController;
import nl.wouterdebruijn.slurp.controller.SidebarController;
import nl.wouterdebruijn.slurp.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.repository.SlurpPlayerRepository;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ReloadConfig implements TabExecutor {
    public ReloadConfig() {
        Objects.requireNonNull(Slurp.getPlugin().getCommand("slurpreload")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Slurp.reload();

        if ((sender instanceof Player player)) {
            MessageController.sendMessage(player, "Slurp has been reloaded");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
