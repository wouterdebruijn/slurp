package dev.krijninc.slurp.eventHandlers;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.exceptions.FetchException;
import dev.krijninc.slurp.helpers.ConsumeHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;

public class PlayerDeathEventHandler extends EventHandler<PlayerDeathEvent> {
    public PlayerDeathEventHandler(double amount, double chance) {
        super(amount, chance);
    }

    @Override
    protected void sendMessage(Player trigger, DrunkEntry entry) {
        if (entry.getSips() >= 1 && entry.getShots() >= 1)
            Slurp.broadcastMessage(trigger.getDisplayName() + " died, now they take " + entry.getSips() + " sips and " + entry.getShots() + " shots!");
        else if (entry.getSips() > 0)
            Slurp.broadcastMessage(trigger.getDisplayName() + " died, now they take " + entry.getSips() + " sips!");
        else
            Slurp.broadcastMessage(trigger.getDisplayName() + " died, now they take " + entry.getShots() + " shots!");
    }

    @Override
    protected void handleEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();

        try {
            ArrayList<DrunkEntry> entries = ConsumeHandler.playerDrinks(player, (int) amount, getRemainingSips());
            sendMessage(player, entries.get(entries.size() - 1));

            buddyNotifier(entries);
        } catch (FetchException e) {
            Slurp.broadcastMessage(ChatColor.DARK_RED + "Internal Server error, check console for details.");
        }
    }
}
