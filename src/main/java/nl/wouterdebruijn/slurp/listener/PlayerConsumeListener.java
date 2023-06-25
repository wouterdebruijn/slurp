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
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerConsumeListener implements Listener {

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        SlurpPlayer sp = SlurpPlayerManager.getPlayer(p);
        if (!SlurpPlayerManager.checkNull(p, sp)) {
            if (RandomGenerator.hasChance(SlurpConfig.getValue(ConfigValue.CHANCES_CHOKING))) {
                final int sips = SlurpConfig.getValue(ConfigValue.SIP_CHOKING);
                final int shots = SlurpConfig.getValue(ConfigValue.SHOT_CHOKING);
                SlurpEntryBuilder entry = new SlurpEntryBuilder(sips, shots, sp.getUuid(), sp.getSession().getUuid(), false, false);
                ConsumableGivingHandler.serverGiveConsumable(p, entry);
            }
        }
    }
}
