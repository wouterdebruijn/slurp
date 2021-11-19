package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import dev.krijninc.slurp.exceptions.FetchException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class CopperOreEventHandler extends BlockBreakEventHandler {
    public CopperOreEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE});
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        DrunkEntry entry = new DrunkEntry(player.getUniqueId(), amount, 0);
        try {
            entry.save();
            Slurp.broadcastMessage(ChatColor.GOLD + player.getDisplayName() + " mined copper, they take " + amount + " sips!");
        } catch (FetchException e) {
            Slurp.sendMessage(player, ChatColor.DARK_RED + "Internal Server error, check console for details.");
        }
    }
}
