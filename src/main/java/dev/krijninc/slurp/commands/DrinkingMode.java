package dev.krijninc.slurp.commands;

import dev.krijninc.slurp.Slurp;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class DrinkingMode implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        try {
            double modifier;
            if (args.length == 0) {
                Slurp.sendMessage(player, String.format("Current modifier is: %.2f", Slurp.getEventListener().getModifier()));
                return true;
            } else {
                modifier = Double.parseDouble(args[0]);
            }

            Slurp.setModifier(modifier);
            Slurp.sendMessage(player, String.format("Drinking modifier is now: %.2f", modifier));

            return true;
        } catch (NumberFormatException e) {
            Slurp.sendMessage(player, ChatColor.RED + "Provided arguments must be a number!");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Arrays.asList("0.5", "1", "1.5", "");
    }
}