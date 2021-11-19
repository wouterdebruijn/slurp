package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import dev.krijninc.slurp.eventHandlers.BlockBreakRandomEventHandler;
import dev.krijninc.slurp.eventHandlers.SipsHandler;
import dev.krijninc.slurp.exceptions.FetchException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;

public class CopperOreEventHandler extends BlockBreakRandomEventHandler {
    public CopperOreEventHandler(double amount, double chance, int eventType) {
        super(amount, chance, new Material[]{Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE}, eventType);
    }

    protected void sendMessage(Player trigger, DrunkEntry entry) {
        if (entry.getPlayer().equals(trigger.getUniqueId())) {
            Slurp.sendMessage(trigger, ChatColor.GOLD + "You mined copper, now take " + entry.getSips() + " sips!");
        } else {
            Player drinker = Slurp.getPlugin().getServer().getPlayer(entry.getPlayer());
            if (drinker == null) return;
            Slurp.sendMessage(drinker, ChatColor.GOLD + trigger.getDisplayName() + " mined copper, now you take " + entry.getSips() + " sips!");
        }
    }
}
