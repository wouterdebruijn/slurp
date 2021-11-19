package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import dev.krijninc.slurp.eventHandlers.ConsumeHandler;
import dev.krijninc.slurp.exceptions.FetchException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;

public class LogsEventHandler extends BlockBreakEventHandler {
    public LogsEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.ACACIA_LOG, Material.BIRCH_LOG, Material.OAK_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.SPRUCE_LOG});
    }

    @Override
    protected void sendMessage(Player trigger, DrunkEntry entry) {
        if (entry.getSips() > 0 && entry.getShots() > 0)
            Slurp.broadcastMessage(trigger.getDisplayName() + " was attacked by termites, they recover their cool by taking " + shotString(entry.getShots(), "") + " and " + sipString(entry.getSips()));
        else if (entry.getSips() > 0)
            Slurp.broadcastMessage(trigger.getDisplayName() + " was attacked by termites, they recover their cool by taking " + sipString(entry.getSips()));
        else
            Slurp.broadcastMessage(trigger.getDisplayName() + " was attacked by termites, they recover their cool by taking " + shotString(entry.getShots()));
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        try {
            Player player = event.getPlayer();
            DrunkEntry entry = ConsumeHandler.serverNoSplit(new ArrayList<>(), 0, (int) amount);
            sendMessage(player, entry);
        } catch (FetchException e) {
            Slurp.broadcastMessage(ChatColor.DARK_RED + "Internal Server error, check console for details.");
        }
    }
}
