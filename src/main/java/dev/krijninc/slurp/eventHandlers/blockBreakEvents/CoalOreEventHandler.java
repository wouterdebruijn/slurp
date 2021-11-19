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

public class CoalOreEventHandler extends BlockBreakEventHandler {
    public CoalOreEventHandler(double amount, double chance, int eventType) {
        super(amount, chance, new Material[]{Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE});
        this.eventType = eventType;
    }

    int eventType;

    private void sendMessage(Player trigger, DrunkEntry entry) {
        if (entry.getPlayer().equals(trigger.getUniqueId())) {
            Slurp.sendMessage(trigger, ChatColor.GOLD + "You mined coal, now take " + entry.getSips() + " sips!");
        } else {
            Player drinker = Slurp.getPlugin().getServer().getPlayer(entry.getPlayer());
            if (drinker == null) return;
            Slurp.sendMessage(drinker, ChatColor.GOLD + trigger.getDisplayName() + " mined coal, now you take " + entry.getSips() + " sips!");
        }
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();

        try {
            if (eventType == 0 || eventType == 1) {
                ArrayList<DrunkEntry> createdEntries = SipsHandler.serverSplit(new ArrayList<>(), 0, (int) amount, eventType == 1);

                for (DrunkEntry entry : createdEntries) {
                    sendMessage(player, entry);
                }
            } else if (eventType == 2) {
                DrunkEntry entry = SipsHandler.serverNoSplit(new ArrayList<>(), 0, (int) amount);
                sendMessage(player, entry);
            } else {
                DrunkEntry entry = SipsHandler.playerDrinks(player, 0, (int) amount);
                sendMessage(player, entry);
            }
        } catch (FetchException e) {
            Slurp.broadcastMessage(ChatColor.DARK_RED + "Internal Server error, check console for details.");
        }
    }
}
