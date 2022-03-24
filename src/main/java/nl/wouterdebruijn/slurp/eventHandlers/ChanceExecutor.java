package nl.wouterdebruijn.slurp.eventHandlers;

import org.bukkit.event.Event;

import java.util.Random;

abstract public class ChanceExecutor<BukkitEvent extends Event> extends Executor<BukkitEvent> {

    static protected Random random = new Random();
    int executionChance;

    public ChanceExecutor(int chance) {
        this.executionChance = chance;
    }

    static protected int generateChange(int chance) {
        return chance;
    }

    @Override
    public void execute(BukkitEvent event) {
        try {
            if (beforeExecution(event) && executionChance >= random.nextInt(101)) {
                onExecution(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
