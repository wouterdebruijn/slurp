package dev.krijninc.slurp.commands;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.entities.DrunkPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TakeSip implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        try {
            int sipCount;
            if (args.length == 0 || Integer.parseInt(args[0]) < 1) {
                sipCount = -1;
            } else {
                sipCount = Integer.parseInt(args[0]) * -1;
            }

            DrunkEntry entry = new DrunkEntry(player.getUniqueId(), sipCount, 0);
            DrunkEntry savedEntry = entry.save();
            player.sendMessage(ChatColor.GREEN + String.format("You have taken %d %s!", savedEntry.getSips() * -1, savedEntry.getSips() * -1 > 1 ? "sips" : "sip"));
            return true;
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Provided arguments must be a number!");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
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
            for (int i=1; i <= drunkPlayer.remaining.sips; i++) {
                options.add(""+i);
            }
            return options;
        }
        return null;
    }
}
