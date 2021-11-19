package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import dev.krijninc.slurp.eventHandlers.SipsHandler;
import dev.krijninc.slurp.exceptions.FetchException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;

public class LapisOreEventHandler extends BlockBreakEventHandler {
    public LapisOreEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE});
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        try {

        ArrayList<DrunkEntry> createdEntries;
        createdEntries = SipsHandler.serverSplit(new ArrayList<>(), 0, (int) amount, false);

        for (DrunkEntry entry : createdEntries) {
            sendMessage(player, entry);
        }
        } catch (FetchException e) {
            Slurp.broadcastMessage(ChatColor.DARK_RED + "Internal Server error, check console for details.");
        }
    }

    protected void sendMessage(Player trigger, DrunkEntry entry) {
        if (entry.getPlayer().equals(trigger.getUniqueId())) {
            Slurp.sendMessage(trigger, ChatColor.GOLD + "You mined lapis, now take " + entry.getSips() + " sips!");
        } else {
            Player drinker = Slurp.getPlugin().getServer().getPlayer(entry.getPlayer());
            if (drinker == null) return;
            Slurp.sendMessage(drinker, ChatColor.GOLD + trigger.getDisplayName() + " mined lapis, now you take " + entry.getSips() + " sips!");
        }
    }
}
