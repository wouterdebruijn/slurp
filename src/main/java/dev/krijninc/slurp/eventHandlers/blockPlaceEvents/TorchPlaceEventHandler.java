package dev.krijninc.slurp.eventHandlers.blockPlaceEvents;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.eventHandlers.BlockPlaceEventHandler;
import dev.krijninc.slurp.eventHandlers.ConsumeHandler;
import dev.krijninc.slurp.exceptions.FetchException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

public class TorchPlaceEventHandler extends BlockPlaceEventHandler {
    public TorchPlaceEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.TORCH, Material.WALL_TORCH});
    }

    @Override
    protected void sendMessage(Player trigger, DrunkEntry entry) {
        if (entry.getSips() > 1) {
            Slurp.broadcastMessage(trigger.getDisplayName() + " placed a torch, that's + " + sipString(entry.getSips()));
        } else {
            Slurp.broadcastMessage(trigger.getDisplayName() + " placed a torch, that's " + sipString(entry.getSips()));
        }
    }

    @Override
    protected void handleEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        try {
            DrunkEntry entry = ConsumeHandler.playerDrinks(player, 0, (int) amount);
            sendMessage(player, entry);
        } catch (FetchException e) {
            Slurp.broadcastMessage(ChatColor.DARK_RED + "Internal Server error, check console for details.");
        }
    }
}
