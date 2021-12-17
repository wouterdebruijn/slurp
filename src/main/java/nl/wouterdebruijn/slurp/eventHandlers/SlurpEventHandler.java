package nl.wouterdebruijn.slurp.eventHandlers;

import nl.wouterdebruijn.slurp.Slurp;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

abstract public class SlurpEventHandler<BukkitEvent extends Event> implements Listener {
    public SlurpEventHandler() {
        Slurp.getPlugin().getServer().getPluginManager().registerEvents(this, Slurp.getPlugin());
    }

    @EventHandler
    protected abstract void onEvent(BukkitEvent event);
}
