package nl.wouterdebruijn.slurp.commands;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.controller.MessageController;
import nl.wouterdebruijn.slurp.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.exceptions.APIPostException;
import nl.wouterdebruijn.slurp.repository.SlurpEntryRepository;
import nl.wouterdebruijn.slurp.repository.SlurpPlayerRepository;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GiveSip implements TabExecutor {
    public GiveSip() {
        Objects.requireNonNull(Slurp.getPlugin().getCommand("givesip")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        SlurpPlayer slurpPlayer = SlurpPlayerRepository.get(player.getUniqueId());

        try {
            int sipCount;
            if (args.length == 0 || Integer.parseInt(args[1]) < 1) {
                MessageController.sendMessage(player, true, ChatColor.RED + "Amount should be an positive number!");
                return true;
            } else {
                sipCount = Integer.parseInt(args[1]) * -1;
            }

            if (sipCount > slurpPlayer.giveable.sips) {
                MessageController.sendMessage(player, true, ChatColor.RED + "Hold up, you don't have that many sips to give away!");
                return true;
            }

            SlurpEntry removeGivableEntry = new SlurpEntry(player.getUniqueId(), 0, sipCount, false, true);
            SlurpEntry addConsumables = new SlurpEntry(Objects.requireNonNull(Slurp.getPlugin().getServer().getPlayer(args[0])).getUniqueId(), 0, sipCount * -1, false, false);
            SlurpEntryRepository.cache(removeGivableEntry);
            SlurpEntryRepository.cache(addConsumables);

            Bukkit.getScheduler().runTaskAsynchronously(Slurp.getPlugin(), () -> {
                try {
                    SlurpEntryRepository.save(removeGivableEntry);
                    SlurpEntryRepository.save(addConsumables);
                } catch (APIPostException e) {
                    e.printStackTrace();
                }
            });

            MessageController.sendMessage(player, true, ChatColor.GREEN + "You have taken " + addConsumables.sips * -1 + " sips(s)!");
            return true;
        } catch (NumberFormatException e) {
            MessageController.sendMessage(player, true, ChatColor.RED + "Amount should be an positive number!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            MessageController.sendMessage(player, true, ChatColor.RED + "Internal Server error, check console for details.");
            return true;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return null;
        }

        if (args.length == 1) {
            ArrayList<String> options = new ArrayList<>();

            for (Player onlinePlayer : Slurp.getPlugin().getServer().getOnlinePlayers()) {
                options.add(onlinePlayer.getName());
            }
        }

        if (args.length == 2) {
            ArrayList<String> options = new ArrayList<>();

            SlurpPlayer slurpPlayer = SlurpPlayerRepository.get(player.getUniqueId());
            for (int i = 1; i <= slurpPlayer.giveable.sips; i++) {
                options.add("" + i);
            }
            return options;
        }
        return null;
    }
}
