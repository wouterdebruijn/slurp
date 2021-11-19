package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import dev.krijninc.slurp.eventHandlers.SipsHandler;
import dev.krijninc.slurp.exceptions.FetchException;
import dev.krijninc.slurp.types.Consumables;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;

public class CoalOreEventHandler extends BlockBreakEventHandler {
    public CoalOreEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE});
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();

        try {
            ArrayList<DrunkEntry> createdEntries = SipsHandler.serverSplit(new ArrayList<>(), 0, (int) amount, true);

            for (DrunkEntry entry : createdEntries) {
                if (entry.getPlayer().equals(player.getUniqueId())) {
                    Slurp.sendMessage(player, ChatColor.GOLD + "You mined coal, now take " + entry.getSips() + " sips!");
                } else {
                    Player drinker = Slurp.getPlugin().getServer().getPlayer(entry.getPlayer());
                    if (drinker == null) return;
                    Slurp.sendMessage(drinker, ChatColor.GOLD + player.getDisplayName() + " mined coal, now you take " + entry.getSips() + " sips!");
                }
            }

        } catch (FetchException e) {
            Slurp.broadcastMessage(ChatColor.DARK_RED + "Internal Server error, check console for details.");
        }
    }
}
