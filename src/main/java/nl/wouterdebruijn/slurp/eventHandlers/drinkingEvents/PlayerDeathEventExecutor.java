package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents;

import nl.wouterdebruijn.slurp.eventHandlers.Executor;
import org.bukkit.event.entity.PlayerDeathEvent;

abstract public class PlayerDeathEventExecutor extends Executor<PlayerDeathEvent> {

    public PlayerDeathEventExecutor() {
        super();
    }

    private String shotName(int shots) {
        return shots > 1 ? "shots" : "shot";
    }

    private String sipName(int sips) {
        return sips > 1 ? "sips" : "sip";
    }
}
