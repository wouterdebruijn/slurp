package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.eventHandlers.BlockBreakRandomEventHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RedstoneOreEventHandler extends BlockBreakRandomEventHandler {
    public RedstoneOreEventHandler(double amount, double chance, int eventType) {
        super(amount, chance, new Material[]{Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE}, eventType);
    }

    protected void sendMessage(Player trigger, DrunkEntry entry) {
        if (entry.getPlayer().equals(trigger.getUniqueId())) {
            Slurp.sendMessage(trigger, ChatColor.GOLD + "You mined redstone, now take " + entry.getSips() + " sips!");
        } else {
            Player drinker = Slurp.getPlugin().getServer().getPlayer(entry.getPlayer());
            if (drinker == null) return;
            Slurp.sendMessage(drinker, ChatColor.GOLD + trigger.getDisplayName() + " mined redstone, now you take " + entry.getSips() + " sips!");
        }
    }
}
