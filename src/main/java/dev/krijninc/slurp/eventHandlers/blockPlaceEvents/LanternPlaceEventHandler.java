package dev.krijninc.slurp.eventHandlers.blockPlaceEvents;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.eventHandlers.BlockPlaceEventHandler;
import dev.krijninc.slurp.helpers.ConsumeHandler;
import dev.krijninc.slurp.exceptions.FetchException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;

public class LanternPlaceEventHandler extends BlockPlaceEventHandler {
    public LanternPlaceEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.LANTERN, Material.SOUL_LANTERN});
    }

    @Override
    protected void sendMessage(Player trigger, DrunkEntry entry) {
        Slurp.broadcastMessage(trigger.getDisplayName() + " placed a lantern, how clever! That's " + sipString(entry.getSips()));
    }

    @Override
    protected void handleEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        try {
            ArrayList<DrunkEntry> entries = ConsumeHandler.playerDrinks(player, 0, (int) amount);
            sendMessage(player, entries.get(entries.size() - 1));

            buddyNotifier(entries);
        } catch (FetchException e) {
            Slurp.broadcastMessage(ChatColor.DARK_RED + "Internal Server error, check console for details.");
        }
    }
}
