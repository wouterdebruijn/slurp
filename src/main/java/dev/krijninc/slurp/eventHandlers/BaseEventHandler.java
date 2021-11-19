package dev.krijninc.slurp.eventHandlers;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import dev.krijninc.slurp.helpers.ConfigLoader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class BaseEventHandler {
    protected double amount;
    protected double chance;

    public BaseEventHandler(double amount, double chance) {
        this.amount = amount;
        this.chance = chance;
    }

    public void execute(PlayerDeathEvent event) {
        if (Math.random() <= chance) {
            handleEvent(event);
        }
    }

    protected void handleEvent(PlayerDeathEvent event) {
    }

    protected int getRemainingSips() {
        return (int) (amount % 1) * ConfigLoader.getInt("shots-to-sips-multiplier");
    }

    protected void sendMessage(Player trigger, DrunkEntry entry) {
        if (entry.getPlayer().equals(trigger.getUniqueId())) {
            Slurp.sendMessage(trigger, ChatColor.GOLD + "You did something! Now take " + entry.getSips() + " sips!");
        } else {
            Player drinker = Slurp.getPlugin().getServer().getPlayer(entry.getPlayer());
            if (drinker == null) return;
            Slurp.sendMessage(drinker, ChatColor.GOLD + trigger.getDisplayName() + " did something, now you take " + entry.getSips() + " sips!");
        }
    }
}