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

public class GiveShot implements TabExecutor {
    public GiveShot() {
        Objects.requireNonNull(Slurp.getPlugin().getCommand("giveshot")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        SlurpPlayer slurpPlayer = SlurpPlayerRepository.get(player.getUniqueId());

        try {
            int shotCount;
            if (args.length == 0 || Integer.parseInt(args[1]) < 1) {
                MessageController.sendMessage(player, true, ChatColor.RED + "Amount should be a positive number!");
                return true;
            } else {
                shotCount = Integer.parseInt(args[1]) * -1;
            }

            if (shotCount * -1 > slurpPlayer.giveable.shots) {
                MessageController.sendMessage(player, true, ChatColor.RED + "Hold up, you don't have that many shots to give away!");
                return true;
            }

            if (shotCount * -1 > 32766) {
                MessageController.sendMessage(player, true, ChatColor.RED + "Hold up, our systems can't process these large numbers :(");
                return true;
            }

            Player receiver = Slurp.getPlugin().getServer().getPlayer(args[0]);

            if (receiver == null) {
                MessageController.sendMessage(player, true, ChatColor.RED + "That player isn't online right now!");
                return true;
            }

            SlurpEntry removeGiveableEntry = new SlurpEntry(player.getUniqueId(), shotCount, 0, false, true);
            SlurpEntry addConsumables = new SlurpEntry(receiver.getUniqueId(), shotCount * -1, 0, false, false);

            MessageController.broadcast(true, ChatColor.GREEN + player.getName() + " gave " + receiver.getName() + " " + addConsumables.shots + " shot(s)!");

            SlurpEntryRepository.cache(removeGiveableEntry, false);
            SlurpEntryRepository.cache(addConsumables, true);

            Bukkit.getScheduler().runTaskAsynchronously(Slurp.getPlugin(), () -> {
                try {
                    SlurpEntryRepository.save(removeGiveableEntry);
                    SlurpEntryRepository.save(addConsumables);
                } catch (APIPostException e) {
                    e.printStackTrace();
                }
            });
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

            for (Player onlinePlayer : Slurp.getPlugin().getServer().getOnlinePlayers()) {
                options.add(onlinePlayer.getName());
            }
            return options;
        }

        if (args.length == 2) {
            ArrayList<String> options = new ArrayList<>();

            SlurpPlayer slurpPlayer = SlurpPlayerRepository.get(player.getUniqueId());
            for (int i = 1; i <= slurpPlayer.giveable.shots; i++) {
                options.add("" + i);
            }
            return options;
        }
        return null;
    }
}
