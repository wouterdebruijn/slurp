package nl.wouterdebruijn.slurp.listener;

import nl.wouterdebruijn.slurp.helper.ConfigValue;
import nl.wouterdebruijn.slurp.helper.RandomGenerator;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.handlers.ConsumableGivingHandler;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMovementListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        SlurpPlayer sp = SlurpPlayerManager.getPlayer(p);
        if (!SlurpPlayerManager.checkNull(p, sp)) {
            SlurpEntryBuilder entry = null;
            if (p.isInsideVehicle() && p.getVehicle() != null && p.getVehicle().getType() == EntityType.BOAT) {
                if (RandomGenerator.hasChance(SlurpConfig.getValue(ConfigValue.CHANCES_BOAT))) {
                    final int sips = SlurpConfig.getValue(ConfigValue.SIP_BOAT);
                    final int shots = SlurpConfig.getValue(ConfigValue.SHOT_BOAT);
                    entry = new SlurpEntryBuilder(sips, shots, sp.getUuid(), sp.getSession().getUuid(), false, false);
                }
            }
            if (p.isSprinting()) {
                if (RandomGenerator.hasChance(SlurpConfig.getValue(ConfigValue.CHANCES_SPRINTING))) {
                    final int sips = SlurpConfig.getValue(ConfigValue.SIP_SPRINTING);
                    final int shots = SlurpConfig.getValue(ConfigValue.SHOT_SPRINTING);
                    entry = new SlurpEntryBuilder(sips, shots, sp.getUuid(), sp.getSession().getUuid(), false, false);
                }
            } else if (p.isSwimming()) {
                if (RandomGenerator.hasChance(SlurpConfig.getValue(ConfigValue.CHANCES_SWIMMING))) {
                    final int sips = SlurpConfig.getValue(ConfigValue.SIP_SWIMMING);
                    final int shots = SlurpConfig.getValue(ConfigValue.SHOT_SWIMMING);
                    entry = new SlurpEntryBuilder(sips, shots, sp.getUuid(), sp.getSession().getUuid(), false, false);
                }
            }
            if (entry != null) {
                ConsumableGivingHandler.serverGiveConsumable(p, entry);
            }
        }
    }
}
