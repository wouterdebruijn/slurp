package dev.krijninc.slurp.eventHandlers;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.helpers.ConfigLoader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;

public class BlockBreakEventHandler {
    protected double amount;
    protected double chance;
    protected final Material[] materials;

    public BlockBreakEventHandler(double amount, double chance, Material[] materials) {
        this.amount = amount;
        this.chance = chance;
        this.materials = materials;
    }

    protected void handleEvent(BlockBreakEvent event) {
    }

    public void execute(BlockBreakEvent event, Material material) {
        if (matchesMaterial(material) && Math.random() <= chance) {
            handleEvent(event);
        }
    }

    protected int getRemainingSips() {
        return (int) (amount % 1) * ConfigLoader.getInt("shots-to-sips-multiplier");
    }

    protected void sendMessage(Player trigger, DrunkEntry entry) {
        if (entry.getPlayer().equals(trigger.getUniqueId())) {
            Slurp.sendMessage(trigger, ChatColor.GOLD + "You mined air, now take " + entry.getSips() + " sips!");
        } else {
            Player drinker = Slurp.getPlugin().getServer().getPlayer(entry.getPlayer());
            if (drinker == null) return;
            Slurp.sendMessage(drinker, ChatColor.GOLD + trigger.getDisplayName() + " mined air, now you take " + entry.getSips() + " sips!");
        }
    }

    protected boolean matchesMaterial(Material material) {
        return Arrays.asList(this.materials).contains(material);
    }
}