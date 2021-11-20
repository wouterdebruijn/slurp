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

public class ConvertSips implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        Slurp.sendMessage(player, ChatColor.RED + "Disabled!");
        return true;

//        try {
//            int sipCount;
//            if (args.length == 0 || Integer.parseInt(args[0]) < 1) {
//                return false;
//            } else if (Integer.parseInt(args[0]) % ConfigLoader.getInt("shots-to-sips-multiplier") > 0) {
//                Slurp.sendMessage(player, ChatColor.RED + " that amount can't be split to sips!");
//                return true;
//            } else {
//                sipCount = Integer.parseInt(args[0]) * -1;
//            }
//
//            int shots = (sipCount * -1) / ConfigLoader.getInt("shots-to-sips-multiplier");
//
//            if (sipCount < -128 || sipCount > 127 || shots < -128 || shots > 127) {
//                Slurp.sendMessage(player, ChatColor.RED + "Sowwy, we *notices buldge* couwdn't pwocess these big nyumbews, pwease pwovide smaww *starts twerking* nyumbews");
//                return true;
//            }
//
//            DrunkEntry entry = new DrunkEntry(true, player.getUniqueId(), sipCount, shots);
//            DrunkEntry savedEntry = entry.save();
//            Slurp.sendMessage(player, ChatColor.GREEN + String.format("You have converted %d sips to %d %s!", savedEntry.getSips() * -1, savedEntry.getShots(), savedEntry.getSips() * -1 > 1 ? "shots" : "shot"));
//            return true;
//        } catch (NumberFormatException e) {
//            Slurp.sendMessage(player, ChatColor.RED + "Provided arguments must be a number!");
//        } catch (FetchException e) {
//            Slurp.sendMessage(player, ChatColor.DARK_RED + "Internal Server error, check console for details.");
//        }
//        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) {
            return null;
        }

        if (args.length == 1) {
            ArrayList<String> options = new ArrayList<>();

            DrunkPlayer drunkPlayer = Slurp.getDrunkPlayer(((Player) sender).getUniqueId());
            for (int i = ConfigLoader.getInt("shots-to-sips-multiplier"); i <= drunkPlayer.remaining.sips; i+=ConfigLoader.getInt("shots-to-sips-multiplier")) {
                options.add("" + i);
            }
            return options;
        }
        return null;
    }
}
