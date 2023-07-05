package nl.wouterdebruijn.slurp.listener;

import nl.wouterdebruijn.slurp.helper.ConfigValue;
import nl.wouterdebruijn.slurp.helper.RandomGenerator;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.handlers.ConsumableGivingHandler;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        SlurpPlayer sp = SlurpPlayerManager.getPlayer(p);
        if (sp == null) {
            p.sendMessage(TextBuilder.error("You are not in a session!"));
            return;
        }
        if (e.getBlock().getType() == Material.STONE) {
            if (RandomGenerator.hasChance(SlurpConfig.getValue(ConfigValue.CHANCES_LUCYSTONE))) {
                final int sips = SlurpConfig.getValue(ConfigValue.SIP_LUCYSTONE);
                final int shots = SlurpConfig.getValue(ConfigValue.SHOT_LUCYSTONE);
                SlurpEntryBuilder entry = new SlurpEntryBuilder(sips, shots, sp.getUuid(), sp.getSession().getUuid(), false, false);
                ConsumableGivingHandler.giveUnifiedSlurp(p, entry);
            }
        } else if (e.getBlock().getType().name().toLowerCase().endsWith("ore")) {

        }
    }
}
