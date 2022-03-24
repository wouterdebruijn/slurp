package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents;

import nl.wouterdebruijn.slurp.eventHandlers.Executor;
import nl.wouterdebruijn.slurp.eventHandlers.SlurpEventHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;

public class BlockPlaceEventHandler extends SlurpEventHandler<BlockPlaceEvent> {

    ArrayList<Executor<BlockPlaceEvent>> chanceExecutors = new ArrayList<>();

    public void registerExecutor(Executor<BlockPlaceEvent> executor) {
        chanceExecutors.add(executor);
    }

    @Override
    @EventHandler
    protected void onEvent(BlockPlaceEvent event) {
        chanceExecutors.forEach((blockBreakEventChanceExecutor -> blockBreakEventChanceExecutor.execute(event)));
    }
}
