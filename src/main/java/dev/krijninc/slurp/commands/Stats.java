package dev.krijninc.slurp.commands;

import dev.krijninc.slurp.Consumables;
import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class Stats implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            return false;
        }
        DrunkPlayer playerStats = Slurp.getDrunkPlayer(player.getUniqueId());

        if (playerStats == null) {
            sender.sendMessage(ChatColor.RED + "You don't have any records yet!");
            return false;
        }

        // Send the current stats for the sender
        sender.sendMessage(String.format(ChatColor.GREEN + "====== Stats ======\n" +
                ChatColor.YELLOW + " - Remaining shots: %d\n - Remaining sips: %d\n - Taken shots: %d\n - Taken sips: %d", playerStats.remaining.shots, playerStats.remaining.sips, playerStats.taken.shots, playerStats.taken.sips));

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
