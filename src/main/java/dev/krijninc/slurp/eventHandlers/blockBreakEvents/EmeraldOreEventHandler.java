package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import dev.krijninc.slurp.eventHandlers.ConsumeHandler;
import dev.krijninc.slurp.exceptions.FetchException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;

public class EmeraldOreEventHandler extends BlockBreakEventHandler {
    public EmeraldOreEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE});
    }

    protected void sendMessage(Player trigger, DrunkEntry entry) {
        if (entry.getPlayer().equals(trigger.getUniqueId())) {
            Slurp.sendMessage(trigger, ChatColor.GOLD + "You mined emeralds, now take " + entry.getSips() + " sips!");
        } else {
            Player drinker = Slurp.getPlugin().getServer().getPlayer(entry.getPlayer());
            if (drinker == null) return;
            Slurp.sendMessage(drinker, ChatColor.GOLD + trigger.getDisplayName() + " mined emeralds, now you take " + entry.getSips() + " sips!");
        }
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();

        try {
            ArrayList<DrunkEntry> createdEntries = ConsumeHandler.everyoneDrinks(new ArrayList<>(List.of(player.getUniqueId())), 0, (int) amount);

            for (DrunkEntry entry : createdEntries) {
                sendMessage(player, entry);
            }
        } catch (FetchException e) {
            Slurp.broadcastMessage(ChatColor.DARK_RED + "Internal Server error, check console for details.");
        }
    }
}
