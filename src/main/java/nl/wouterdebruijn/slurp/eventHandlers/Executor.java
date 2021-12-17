package nl.wouterdebruijn.slurp.eventHandlers;

import org.bukkit.event.Event;

abstract public class Executor<BukkitEvent extends Event> {
    public void execute(BukkitEvent event) {
        if (beforeExecution(event))
            onExecution(event);
    }

    abstract protected void onExecution(BukkitEvent event);

    protected boolean beforeExecution(BukkitEvent event) {
        return true;
    }
}
