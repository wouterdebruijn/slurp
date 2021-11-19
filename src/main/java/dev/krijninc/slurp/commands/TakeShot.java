package dev.krijninc.slurp.commands;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.entities.DrunkPlayer;
import dev.krijninc.slurp.exceptions.FetchException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TakeShot implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        try {
            int shotCount;
            if (args.length == 0 || Integer.parseInt(args[0]) < 1) {
                shotCount = -1;
            } else {
                shotCount = Integer.parseInt(args[0]) * -1;
            }

            DrunkEntry entry = new DrunkEntry(player.getUniqueId(), 0, shotCount);
            DrunkEntry savedEntry = entry.save();
            Slurp.sendMessage(player, ChatColor.GREEN + String.format("You have taken %d %s!", savedEntry.getShots() * -1, savedEntry.getShots() * -1 > 1 ? "shots" : "shot"));
            return true;
        } catch (NumberFormatException e) {
            Slurp.sendMessage(player, ChatColor.RED + "Provided arguments must be a number!");
        } catch (FetchException e) {
            Slurp.sendMessage(player, ChatColor.DARK_RED + "Internal Server error, check console for details.");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) {
            return null;
        }

        if (args.length == 1) {
            ArrayList<String> options = new ArrayList<>();

            DrunkPlayer drunkPlayer = Slurp.getDrunkPlayer(((Player) sender).getUniqueId());
            for (int i = 1; i <= drunkPlayer.remaining.shots; i++) {
                options.add("" + i);
            }
            return options;
        }
        return null;
    }
}
