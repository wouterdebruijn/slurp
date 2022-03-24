package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents;

import nl.wouterdebruijn.slurp.eventHandlers.Executor;
import nl.wouterdebruijn.slurp.eventHandlers.SlurpEventHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;

public class PlayerDeathEventHandler extends SlurpEventHandler<PlayerDeathEvent> {

    ArrayList<Executor<PlayerDeathEvent>> executors = new ArrayList<>();

    public void registerExecutor(Executor<PlayerDeathEvent> executor) {
        executors.add(executor);
    }


    @Override
    @EventHandler
    protected void onEvent(PlayerDeathEvent event) {
        executors.forEach((executor -> executor.execute(event)));
    }
}
