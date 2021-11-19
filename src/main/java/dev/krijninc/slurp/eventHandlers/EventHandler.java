package dev.krijninc.slurp.eventHandlers;

import dev.krijninc.slurp.entities.DrunkEntry;
import org.bukkit.entity.Player;

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
        return amount > 1 ? amount + "shots!" : "a shot!" + suffix;
    }

    protected String sipString(int amount, String suffix) {
        return amount > 1 ? amount + "sips!" : "a sip" + suffix;
    }

    protected String shotString(int amount) {
        return shotString(amount, "!");
    }

    protected String sipString(int amount) {
        return sipString(amount, "!");
    }

    protected abstract void sendMessage(Player trigger, DrunkEntry entry);
    protected abstract void handleEvent(Event event);
}