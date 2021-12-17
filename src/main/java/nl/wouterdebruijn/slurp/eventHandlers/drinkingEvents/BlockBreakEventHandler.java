package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents;

import nl.wouterdebruijn.slurp.eventHandlers.Executor;
import nl.wouterdebruijn.slurp.eventHandlers.SlurpEventHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;

public class BlockBreakEventHandler extends SlurpEventHandler<BlockBreakEvent> {

    ArrayList<Executor<BlockBreakEvent>> chanceExecutors = new ArrayList<>();

    public void registerExecutor(Executor<BlockBreakEvent> executor) {
        chanceExecutors.add(executor);
    }

    @Override
    @EventHandler
    protected void onEvent(BlockBreakEvent event) {
        chanceExecutors.forEach((blockBreakEventChanceExecutor -> blockBreakEventChanceExecutor.execute(event)));
    }
}
