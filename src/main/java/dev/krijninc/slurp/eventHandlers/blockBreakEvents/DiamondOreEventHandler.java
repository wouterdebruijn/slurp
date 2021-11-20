package dev.krijninc.slurp.eventHandlers.blockBreakEvents;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.eventHandlers.BlockBreakEventHandler;
import dev.krijninc.slurp.exceptions.FetchException;
import dev.krijninc.slurp.helpers.ConsumeHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiamondOreEventHandler extends BlockBreakEventHandler {
    public DiamondOreEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE});
    }

    protected void sendMessage(Player trigger, DrunkEntry entry) {
        if (entry.getSips() >= 1 && entry.getShots() >= 1)
            Slurp.broadcastMessage(trigger.getDisplayName() + " mined diamonds, now " + Objects.requireNonNull(Slurp.getPlugin().getServer().getPlayer(entry.getPlayer())).getDisplayName() + " takes " + sipString(entry.getSips(), " and ") + shotString(entry.getShots()));
        else if (entry.getSips() > 0)
            Slurp.broadcastMessage(trigger.getDisplayName() + " mined diamonds, now " + Objects.requireNonNull(Slurp.getPlugin().getServer().getPlayer(entry.getPlayer())).getDisplayName() + " takes " + sipString(entry.getSips()));
        else
            Slurp.broadcastMessage(trigger.getDisplayName() + " mined diamonds, now " + Objects.requireNonNull(Slurp.getPlugin().getServer().getPlayer(entry.getPlayer())).getDisplayName() + " takes " + shotString(entry.getShots()));
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        try {
            ArrayList<DrunkEntry> createdEntries = ConsumeHandler.serverSplit(new ArrayList<>(List.of(player.getUniqueId())), (int) amount, getRemainingSips(), false);

            for (DrunkEntry entry : createdEntries) {
                sendMessage(player, entry);
            }

            buddyNotifier(createdEntries);
        } catch (FetchException e) {
            Slurp.broadcastMessage(ChatColor.DARK_RED + "Internal Server error, check console for details.");
        }
    }
}
