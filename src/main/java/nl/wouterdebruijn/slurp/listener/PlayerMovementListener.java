package nl.wouterdebruijn.slurp.listener;

import nl.wouterdebruijn.slurp.helper.RandomGenerator;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.handlers.ConsumableGivingHandler;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMovementListener implements Listener {
    final int sprintingChance = 1000000;// Chance a player stumble while sprinting (1:1.000.000)
    final int toDrinkSprinting = 2;
    final int swimmingChance = 100000; // Chance a player might get cramp while swimming (1:1.000.000)
    final int toDrinkSwimming = 2;
    final int boatChance = 1000000; // Chance a player's boat might leak (1:1.000.000)
    final int toDrinkBoat = 3;

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        SlurpPlayer sp = SlurpPlayerManager.getPlayer(p);
        if (!SlurpPlayerManager.checkNull(p, sp)) {
            SlurpEntryBuilder entry = null;
            if (p.isInsideVehicle() && p.getVehicle() != null && p.getVehicle().getType() == EntityType.BOAT) {
                if (RandomGenerator.hasChance(boatChance)) {
                    entry = new SlurpEntryBuilder(toDrinkBoat, 0, sp.getUuid(), sp.getSession().getUuid(), false, false);
                }
            }
            if (p.isSprinting()) {
                if (RandomGenerator.hasChance(sprintingChance)) {
                    entry = new SlurpEntryBuilder(toDrinkSprinting, 0, sp.getUuid(), sp.getSession().getUuid(), false, false);
                }
            } else if (p.isSwimming()) {
                if (RandomGenerator.hasChance(swimmingChance)) {
                    entry = new SlurpEntryBuilder(toDrinkSwimming, 0, sp.getUuid(), sp.getSession().getUuid(), false, false);
                }
            }
            if (entry != null) {
                ConsumableGivingHandler.serverGiveConsumable(p, entry);
            }
        }
    }
}
