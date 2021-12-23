package nl.wouterdebruijn.slurp.commands;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.controller.ConfigController;
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

public class ConvertShot implements TabExecutor {
    public ConvertShot() {
        Objects.requireNonNull(Slurp.getPlugin().getCommand("convertshot")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        SlurpPlayer slurpPlayer = SlurpPlayerRepository.get(player.getUniqueId());

        try {
            int shotCount;
            if (args.length == 0 || Integer.parseInt(args[0]) < 1) {
                MessageController.sendMessage(player, true, ChatColor.RED + "Amount should be a positive number!");
                return true;
            } else {
                shotCount = Integer.parseInt(args[0]) * -1;
            }

            if (shotCount * -1 > slurpPlayer.remaining.shots) {
                MessageController.sendMessage(player, true, ChatColor.RED + "Hold up, you don't have that many shot(s)!");
                return true;
            }

            int sipCount = (shotCount * -1) * ConfigController.getInt("shots-to-sips-multiplier");

            SlurpEntry removeConsumables = new SlurpEntry(player.getUniqueId(), shotCount, 0, true, false);
            SlurpEntry addConsumables = new SlurpEntry(player.getUniqueId(), 0, sipCount, false, false);
            SlurpEntryRepository.cache(removeConsumables);
            SlurpEntryRepository.cache(addConsumables);

            Bukkit.getScheduler().runTaskAsynchronously(Slurp.getPlugin(), () -> {
                try {
                    SlurpEntryRepository.save(removeConsumables);
                    SlurpEntryRepository.save(addConsumables);
                } catch (APIPostException e) {
                    e.printStackTrace();
                }
            });
            MessageController.sendMessage(player,true, ChatColor.GREEN + "Converted " + shotCount * -1 + " shot(s) to " + sipCount + " sip(s)!");
            return true;
        } catch (NumberFormatException e) {
            MessageController.sendMessage(player, true, ChatColor.RED + "Amount should be a positive number!");
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

            SlurpPlayer slurpPlayer = SlurpPlayerRepository.get(player.getUniqueId());
            for (int i = 1; i <= slurpPlayer.remaining.shots; i++) {
                options.add("" + i);
            }
            return options;
        }
        return null;
    }
}
