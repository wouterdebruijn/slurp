package dev.krijninc.slurp.commands;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.entities.DrunkPlayer;
import dev.krijninc.slurp.exceptions.FetchException;
import dev.krijninc.slurp.helpers.ConfigLoader;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ConvertShots implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        try {
            int shotCount;
            if (args.length == 0 || Integer.parseInt(args[0]) < 0 ) {
                return false;
            } else {
                shotCount = Integer.parseInt(args[0]) * -1;
            }

            int sips = (shotCount * -1) * ConfigLoader.getInt("shots-to-sips-multiplier");

            if (sips < -128 || sips > 127 || shotCount < -128 || shotCount > 127) {
                Slurp.sendMessage(player, ChatColor.RED + "Sowwy, we *notices buldge* couwdn't pwocess these big nyumbews, pwease pwovide smaww *starts twerking* nyumbews");
                return true;
            }

            DrunkEntry entry = new DrunkEntry(true, player.getUniqueId(), sips, shotCount);
            DrunkEntry savedEntry = entry.save();
            Slurp.sendMessage(player, ChatColor.GREEN + String.format("You have converted %d %s to %d sips!", savedEntry.getShots() * -1, savedEntry.getSips() * -1 > 1 ? "shots" : "shot", savedEntry.getSips()));
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
