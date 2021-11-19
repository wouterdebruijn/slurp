package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.eventHandlers.BlockBreakRandomEventHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;


public class CopperOreEventHandler extends BlockBreakRandomEventHandler {
    public CopperOreEventHandler(double amount, double chance, int eventType) {
        super(amount, chance, new Material[]{Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE}, eventType);
    }

    protected void sendMessage(Player trigger, DrunkEntry entry) {
        if (entry.getPlayer().equals(trigger.getUniqueId())) {
            Slurp.sendMessage(trigger, ChatColor.GOLD + "You mined copper, now take " + sipString(entry.getSips()));
        } else {
            Player drinker = Slurp.getPlugin().getServer().getPlayer(entry.getPlayer());
            if (drinker == null) return;
            Slurp.sendMessage(drinker, ChatColor.GOLD + trigger.getDisplayName() + " mined copper, now you take " + sipString(entry.getSips()));
        }
    }
}
