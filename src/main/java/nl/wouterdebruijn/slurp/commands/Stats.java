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

public class Stats implements TabExecutor {
    public Stats() {
        Objects.requireNonNull(Slurp.getPlugin().getCommand("stats")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        SlurpPlayer slurpPlayer = SlurpPlayerRepository.get(player.getUniqueId());

        MessageController.sendMessage(player,
                ChatColor.YELLOW + "-- Stats " + player.getName() + " --\n" +
                        ChatColor.GREEN + " Taken:\n" +
                        ChatColor.GOLD + "  Sips: " + slurpPlayer.taken.sips + " and Shots: " + slurpPlayer.taken.shots +
                        ChatColor.RED + "\n Remaining:\n" +
                        ChatColor.GOLD + "  Sips: " + slurpPlayer.remaining.sips + " and Shots: " + slurpPlayer.remaining.shots +
                        ChatColor.GREEN + "\n Giveable:\n" +
                        ChatColor.GOLD + "  Sips: " + slurpPlayer.giveable.sips + " and Shots: " + slurpPlayer.giveable.shots
        );

        if (slurpPlayer.isDrinkingBuddy) {
            MessageController.sendMessage(player,
                    ChatColor.BLUE + "\n You are a drinking buddies with: "
            );
            for (SlurpPlayer buddy : SlurpPlayerRepository.getDrinkingBuddies()) {
                MessageController.sendMessage(player, ChatColor.AQUA + " - " + buddy.getMinecraftPlayer().getName());
            }
        }

        slurpPlayer.isDrinkingBuddy = true;

        SlurpPlayerRepository.put(slurpPlayer);

        SidebarController.createSidebar(slurpPlayer);
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return null;
        }

        player.sendMessage(String.valueOf(args.length));
        return null;
    }
}
