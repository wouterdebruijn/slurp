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

public class StoneEventHandler extends BlockBreakEventHandler {
    public StoneEventHandler(double amount, double chance) {
        super(amount, chance, new Material[]{Material.STONE, Material.DEEPSLATE});
    }

    protected void sendMessage(Player trigger, DrunkEntry entry) {
        if (entry.getPlayer().equals(trigger.getUniqueId())) {
            if (entry.getSips() > 0 && entry.getShots() < 1)
                Slurp.sendMessage(trigger, ChatColor.GOLD + "You found a " + ChatColor.GREEN + "lucky" + ChatColor.GOLD + " stone now take " + entry.getSips() + " sips and " + entry.getShots() + " shots!");
            else if (entry.getSips() > 0)
                Slurp.sendMessage(trigger, ChatColor.GOLD + "You found a " + ChatColor.GREEN + "lucky" + ChatColor.GOLD + " stone now take " + entry.getSips() + " sips!");
            else
                Slurp.sendMessage(trigger, ChatColor.GOLD + "You found a " + ChatColor.GREEN + "lucky" + ChatColor.GOLD + " stone now take " + entry.getShots() + " shots!");

        } else {
            Player drinker = Slurp.getPlugin().getServer().getPlayer(entry.getPlayer());
            if (drinker == null) return;
            if (entry.getSips() > 0 && entry.getShots() < 1)
                Slurp.sendMessage(drinker, ChatColor.GOLD + trigger.getDisplayName() + " found a " + ChatColor.GREEN + "lucky" + ChatColor.GOLD + " stone now you take " + entry.getSips() + " sips and " + entry.getShots() + " shots!");
            else if (entry.getSips() > 0)
                Slurp.sendMessage(drinker, ChatColor.GOLD + trigger.getDisplayName() + " found a " + ChatColor.GREEN + "lucky" + ChatColor.GOLD + " stone now you take " + entry.getSips() + " sips!");
            else
                Slurp.sendMessage(drinker, ChatColor.GOLD + trigger.getDisplayName() + " found a " + ChatColor.GREEN + "lucky" + ChatColor.GOLD + " stone now you take " + entry.getShots() + " shots!");
        }
    }

    @Override
    protected void handleEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        try {
            ArrayList<DrunkEntry> createdEntries = ConsumeHandler.everyoneDrinks(new ArrayList<>(), (int) amount, getRemainingSips());

            for (DrunkEntry entry : createdEntries) {
                sendMessage(player, entry);
            }
        } catch (FetchException e) {
            Slurp.broadcastMessage(ChatColor.DARK_RED + "Internal Server error, check console for details.");
        }
    }
}
