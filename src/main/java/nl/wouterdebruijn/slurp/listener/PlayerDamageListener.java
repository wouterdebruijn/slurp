package nl.wouterdebruijn.slurp.listener;

import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.handlers.ConsumableGivingHandler;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener implements Listener {
    final int fallDmgChance = 1000000; // Chance a player might break his ankle upon taking fall damage
    final int toDrink = 2;

    @EventHandler
    public void onDmg(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            SlurpPlayer sp = SlurpPlayerManager.getPlayer(p);
            if (!SlurpPlayerManager.checkNull(p, sp)) {
                if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    SlurpEntryBuilder entry = new SlurpEntryBuilder(toDrink, 0, sp.getUuid(), sp.getSession().getUuid(), false, false);
                    ConsumableGivingHandler.serverGiveConsumable(p, entry);
                }
            }
        }
    }
}
