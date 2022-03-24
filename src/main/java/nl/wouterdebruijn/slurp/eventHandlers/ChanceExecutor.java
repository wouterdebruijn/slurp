package nl.wouterdebruijn.slurp.eventHandlers;

import org.bukkit.event.Event;

import java.util.Random;

abstract public class ChanceExecutor<BukkitEvent extends Event> extends Executor<BukkitEvent> {

    static protected Random random = new Random();
    double executionChance;

    public ChanceExecutor(double chance) {
        this.executionChance = chance;
    }

    static protected double generateChange(double chance) {
        return chance;
    }

    @Override
    public void execute(BukkitEvent event) {
        try {
            if (beforeExecution(event) && executionChance >= random.nextDouble()) {
                onExecution(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
