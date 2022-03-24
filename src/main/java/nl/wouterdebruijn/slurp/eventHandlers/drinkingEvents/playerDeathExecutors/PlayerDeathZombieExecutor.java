package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents.playerDeathExecutors;

import nl.wouterdebruijn.slurp.controller.MessageController;
import nl.wouterdebruijn.slurp.eventHandlers.Executor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathZombieExecutor extends Executor<PlayerDeathEvent> {
    @Override
    protected void onExecution(PlayerDeathEvent event) {
        Player deadman = event.getPlayer();
        playerDrinkEvent(deadman.getUniqueId(), 0, 5, false);
        MessageController.broadcast(true, String.format("%s was killed by a Zombie, now they take 5 sips!", deadman.getName()));
    }

    @Override
    protected boolean beforeExecution(PlayerDeathEvent event) {
        EntityDamageEvent entityDamageEvent = event.getPlayer().getLastDamageCause();

        if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) entityDamageEvent).getDamager();
            return damager instanceof Zombie;
        }
        return false;
    }
}
