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
import java.util.List;

public class DiamondOreEventHandler extends BlockBreakEventHandler {
    public DiamondOreEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE});
    }

    protected void sendMessage(Player trigger, DrunkEntry entry) {
        if (entry.getPlayer().equals(trigger.getUniqueId())) {
            if (entry.getSips() > 0 && entry.getShots() > 0)
                Slurp.sendMessage(trigger, ChatColor.GOLD + "You mined diamonds, now take " + sipString(entry.getSips(), "and ") + shotString(entry.getShots()));
            else if (entry.getSips() > 0)
                Slurp.sendMessage(trigger, ChatColor.GOLD + "You mined diamonds, now take " + entry.getSips() + " sips!");
            else
                Slurp.sendMessage(trigger, ChatColor.GOLD + "You mined diamonds, now take " + shotString(entry.getShots()));

        } else {
            Player drinker = Slurp.getPlugin().getServer().getPlayer(entry.getPlayer());
            if (drinker == null) return;
            if (entry.getSips() > 0 && entry.getShots() > 0)
                Slurp.sendMessage(drinker, ChatColor.GOLD + trigger.getDisplayName() + " mined diamonds, now you take " + sipString(entry.getSips(), "and ") + shotString(entry.getShots()));
            else if (entry.getSips() > 0)
                Slurp.sendMessage(drinker, ChatColor.GOLD + trigger.getDisplayName() + " mined diamonds, now you take " + entry.getSips() + " sips!");
            else
                Slurp.sendMessage(drinker, ChatColor.GOLD + trigger.getDisplayName() + " mined diamonds, now you take " + shotString(entry.getShots()));
        }
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        try {
            ArrayList<DrunkEntry> createdEntries = ConsumeHandler.serverSplit(new ArrayList<>(List.of(player.getUniqueId())), (int) amount, getRemainingSips(), false);

            for (DrunkEntry entry : createdEntries) {
                sendMessage(player, entry);
            }
        } catch (FetchException e) {
            Slurp.broadcastMessage(ChatColor.DARK_RED + "Internal Server error, check console for details.");
        }
    }
}
