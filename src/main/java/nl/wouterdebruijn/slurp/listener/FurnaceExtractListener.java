package nl.wouterdebruijn.slurp.listener;

import nl.wouterdebruijn.slurp.helper.ConfigValue;
import nl.wouterdebruijn.slurp.helper.RandomGenerator;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.handlers.ConsumableGivingHandler;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceExtractEvent;

public class FurnaceExtractListener implements Listener {

    @EventHandler
    public void onExtract(FurnaceExtractEvent e) {
        Player p = e.getPlayer();
        SlurpPlayer sp = SlurpPlayerManager.getPlayer(p);
        if (!SlurpPlayerManager.checkNull(p, sp)) {
            if (RandomGenerator.hasChance(SlurpConfig.getValue(ConfigValue.CHANCES_BURNING_HANDS))) {
                final int sips = SlurpConfig.getValue(ConfigValue.SIP_BURNING_HANDS);
                final int shots = SlurpConfig.getValue(ConfigValue.SHOT_BURNING_HANDS);
                SlurpEntryBuilder entry = new SlurpEntryBuilder(sips, shots, sp.getUuid(), sp.getSession().getUuid(), false, false);
                ConsumableGivingHandler.serverGiveConsumable(p, entry);
            }
        }
    }
}
