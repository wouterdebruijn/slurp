package dev.krijninc.slurp.eventHandlers;

import dev.krijninc.slurp.Slurp;
import dev.krijninc.slurp.entities.DrunkEntry;
import org.bukkit.entity.Player;

import java.util.ArrayList;

abstract public class EventHandler<Event> {
    protected double amount;
    protected double chance;

    public EventHandler(double amount, double chance) {
        this.amount = amount;
        this.chance = chance;
    }

    public void execute(Event event) {
        if (Math.random() <= chance) {
            handleEvent(event);
        }
    }

    protected int getRemainingSips() {
        return (int) ((amount % 1) * 20);
    }

    protected String shotString(int amount, String suffix) {
        return amount > 1 ? amount + " shots" + suffix : "a shot" + suffix;
    }

    protected String sipString(int amount, String suffix) {
        return amount > 1 ? amount + " sips" + suffix : "a sip" + suffix;
    }

    protected String shotString(int amount) {
        return shotString(amount, "!");
    }

    protected String sipString(int amount) {
        return sipString(amount, "!");
    }

    protected void buddyNotifier(ArrayList<DrunkEntry> entries) {
        for (DrunkEntry entry : entries) {
            if (entry.isBuddyEntry()) {
                Player player = Slurp.getPlugin().getServer().getPlayer(entry.getPlayer());
                if (player != null) {
                    if (entry.getSips() > 0 && entry.getShots() >= 1)
                        Slurp.sendMessage(player, "Your drinking buddy gave you " + sipString(entry.getSips(), " and ") + shotString(entry.getShots()));
                    else if (entry.getSips() > 0)
                        Slurp.sendMessage(player, "Your drinking buddy gave you " + sipString(entry.getSips()));
                    else
                        Slurp.sendMessage(player, "Your drinking buddy gave you " + shotString(entry.getShots()));
                }
            }
        }
    }

    protected abstract void sendMessage(Player trigger, DrunkEntry entry);

    protected abstract void handleEvent(Event event);
}