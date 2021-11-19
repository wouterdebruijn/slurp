package dev.krijninc.slurp.eventHandlers;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.exceptions.FetchException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathEventHandler extends BaseEventHandler {
    public PlayerDeathEventHandler(double amount, double chance) {
        super(amount, chance);
    }

    @Override
    protected void handleEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Slurp.broadcastMessage("Death event22!");

        try {
            DrunkEntry entry = SipsHandler.playerDrinks(player, (int) amount, getRemainingSips());

            if (entry.getSips() > 0 && entry.getShots() < 1)
                Slurp.broadcastMessage(ChatColor.GOLD + player.getDisplayName() + "died, now they take " + entry.getSips() + " sips and " + entry.getShots() + " shots!");
            else if (entry.getSips() > 0)
                Slurp.broadcastMessage(ChatColor.GOLD + player.getDisplayName() + "died, now they take " + entry.getSips() + " sips!");
            else
                Slurp.broadcastMessage(ChatColor.GOLD + player.getDisplayName() + "died, now they take " + entry.getShots() + " shots!");
        } catch (FetchException e) {
            Slurp.broadcastMessage(ChatColor.DARK_RED + "Internal Server error, check console for details.");
        }
    }

    @Override
    protected void sendMessage(Player trigger, DrunkEntry entry) {
        super.sendMessage(trigger, entry);
    }
}
