package nl.wouterdebruijn.slurp.listener;

import nl.wouterdebruijn.slurp.helper.RandomGenerator;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.handlers.ConsumableGivingHandler;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerConsumeListener implements Listener {
    final int chokeChance = 1000000; // Chance a player might choke on consumables (1:1.000.000)
    final int toDrink = 4;

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        SlurpPlayer sp = SlurpPlayerManager.getPlayer(p);
        if (!SlurpPlayerManager.checkNull(p, sp)) {
            if (RandomGenerator.hasChance(chokeChance)) {
                SlurpEntryBuilder entry = new SlurpEntryBuilder(toDrink, 0, sp.getUuid(), sp.getSession().getUuid(), false, false);
                ConsumableGivingHandler.serverGiveConsumable(p, entry);
            }
        }
    }
}
