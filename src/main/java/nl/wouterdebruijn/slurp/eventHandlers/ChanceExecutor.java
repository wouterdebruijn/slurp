package nl.wouterdebruijn.slurp.eventHandlers;

import org.bukkit.event.Event;

import java.util.Random;

abstract public class ChanceExecutor<BukkitEvent extends Event> extends Executor<BukkitEvent> {

    int executionChance;
    Random random = new Random();

    public ChanceExecutor(int chance) {
        this.executionChance = chance;
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
