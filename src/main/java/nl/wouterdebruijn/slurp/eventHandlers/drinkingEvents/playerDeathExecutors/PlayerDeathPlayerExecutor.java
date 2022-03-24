package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.playerDeathExecutors;

import nl.wouterdebruijn.slurp.controller.MessageController;
import nl.wouterdebruijn.slurp.eventHandlers.Executor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathPlayerExecutor extends Executor<PlayerDeathEvent> {
    @Override
    protected void onExecution(PlayerDeathEvent event) {
        event.deathMessage(null);

        Player killer = event.getPlayer().getKiller();
        Player deadman = event.getPlayer();

        if (killer == null)
            return;

        playerDrinkEvent(killer.getUniqueId(), 1, 0, false);
        playerDrinkEvent(deadman.getUniqueId(), 1, 0, false);

        MessageController.broadcast(true, String.format("%s killed %s, now they both take a shot!", killer.getName(), deadman.getName()));
    }

    @Override
    protected boolean beforeExecution(PlayerDeathEvent event) {
        return event.getPlayer().getKiller() != null;
    }
}
