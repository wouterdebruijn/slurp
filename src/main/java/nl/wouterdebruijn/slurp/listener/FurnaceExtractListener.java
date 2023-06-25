package nl.wouterdebruijn.slurp.listener;

import nl.wouterdebruijn.slurp.helper.RandomGenerator;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.handlers.ConsumableGivingHandler;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceExtractEvent;

public class FurnaceExtractListener implements Listener {
    final int burnHands = 1000000; // Chance a player might burn his hands when taking something out of an oven (1:1.000.000)
    final int toDrink = 5;

    @EventHandler
    public void onExtract(FurnaceExtractEvent e) {
        Player p = e.getPlayer();
        SlurpPlayer sp = SlurpPlayerManager.getPlayer(p);
        if (!SlurpPlayerManager.checkNull(p, sp)) {
            if (RandomGenerator.hasChance(burnHands)) {
                SlurpEntryBuilder entry = new SlurpEntryBuilder(toDrink, 0, sp.getUuid(), sp.getSession().getUuid(), false, false);
                ConsumableGivingHandler.serverGiveConsumable(p, entry);
            }
        }
    }
}
