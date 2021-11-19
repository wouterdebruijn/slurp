package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import dev.krijninc.slurp.eventHandlers.BlockBreakRandomEventHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class IronOreEventHandler extends BlockBreakRandomEventHandler {
    public IronOreEventHandler(double amount, double chance, int eventType) {
        super(amount, chance, new Material[]{Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE}, eventType);
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
    }

    protected void sendMessage(Player trigger, DrunkEntry entry) {
        if (entry.getPlayer().equals(trigger.getUniqueId())) {
            Slurp.sendMessage(trigger, ChatColor.GOLD + "You mined iron, now take " + entry.getSips() + " sips!");
        } else {
            Player drinker = Slurp.getPlugin().getServer().getPlayer(entry.getPlayer());
            if (drinker == null) return;
            Slurp.sendMessage(drinker, ChatColor.GOLD + trigger.getDisplayName() + " mined iron, now you take " + entry.getSips() + " sips!");
        }
    }
}
