package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.playerDeathExecutors;

import nl.wouterdebruijn.slurp.controller.MessageController;
import nl.wouterdebruijn.slurp.eventHandlers.Executor;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathBlazeExecutor extends Executor<PlayerDeathEvent> {
    @Override
    protected void onExecution(PlayerDeathEvent event) {
        Player deadman = event.getPlayer();
        playerDrinkEvent(deadman.getUniqueId(), 0, 8, false);
        MessageController.broadcast(true, String.format("%s was killed by a Blaze, now they take 10 sips!", deadman.getName()));
    }

    @Override
    protected boolean beforeExecution(PlayerDeathEvent event) {
        EntityDamageEvent entityDamageEvent = event.getPlayer().getLastDamageCause();

        if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) entityDamageEvent).getDamager();
            return damager instanceof Blaze;
        }
        return false;
    }
}
