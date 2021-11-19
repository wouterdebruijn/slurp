package dev.krijninc.slurp.eventHandlers;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.exceptions.FetchException;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class BlockBreakRandomEventHandler extends BlockBreakEventHandler {
    protected int eventType;

    public BlockBreakRandomEventHandler(double amount, double chance, Material[] materials, int eventType) {
        super(amount, chance, materials);
        this.eventType = eventType;
    }

    public void execute(BlockBreakEvent event, Material material) {
        if (matchesMaterial(material) && Math.random() <= chance) {
            handleEvent(event);
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

    private boolean matchesMaterial(Material material) {
        return Arrays.asList(this.materials).contains(material);
    }
}