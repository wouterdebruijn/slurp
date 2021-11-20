package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.eventHandlers.BlockBreakRandomEventHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Objects;

public class IronOreEventHandler extends BlockBreakRandomEventHandler {
    public IronOreEventHandler(double amount, double chance, int eventType) {
        super(amount, chance, new Material[]{Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE}, eventType);
    }

    protected void sendMessage(Player trigger, DrunkEntry entry) {
        Slurp.broadcastMessage(trigger.getDisplayName() + " mined iron, now " + Objects.requireNonNull(Slurp.getPlugin().getServer().getPlayer(entry.getPlayer())).getDisplayName() + " takes " + sipString(entry.getSips()));
    }
}
