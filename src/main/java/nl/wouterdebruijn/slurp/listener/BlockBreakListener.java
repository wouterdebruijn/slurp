package nl.wouterdebruijn.slurp.listener;

import nl.wouterdebruijn.slurp.helper.RandomGenerator;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.handlers.ConsumableGivingHandler;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    final int lucyStone = 1000000; // Chance a player mines a lucky stone, dubbed 'lucy stone' (1:1.000.000)
    final int toShot = 1;

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        SlurpPlayer sp = SlurpPlayerManager.getPlayer(p);
        if (sp == null) {
            p.sendMessage(TextBuilder.error("You are not in a session!"));
            return;
        }
        if (RandomGenerator.hasChance(lucyStone)) {
            SlurpEntryBuilder entry = new SlurpEntryBuilder(0, toShot, sp.getUuid(), sp.getSession().getUuid(), false, false);
            ConsumableGivingHandler.giveUnifiedSlurp(p, entry);
        }
    }
}
