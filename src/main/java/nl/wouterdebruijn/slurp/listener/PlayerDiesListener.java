package nl.wouterdebruijn.slurp.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nl.wouterdebruijn.slurp.helper.ConfigValue;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.handlers.ConsumableGivingHandler;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDiesListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        SlurpPlayer sp = SlurpPlayerManager.getPlayer(p);
        if (sp == null) {
            p.sendMessage(TextBuilder.error("You are not in a session!"));
            return;
        }
        if (p.getKiller() == null) {
            // Player has not been killed by an entity, died otherwise
            // Add sips to player
            final int sips = SlurpConfig.getValue(ConfigValue.SIP_DIE);
            final int shots = SlurpConfig.getValue(ConfigValue.SHOT_DIE);
            SlurpEntryBuilder entry = new SlurpEntryBuilder(sips, shots, sp.getUuid(), sp.getSession().getUuid(), false, false);
            ConsumableGivingHandler.serverGiveConsumable(p, entry);
        } else {
            // Player has been killed by an entity
            Player killerEntity = e.getPlayer().getKiller();
            if (killerEntity != null) {
                // Give the killer a shot for killing a fellow player
                SlurpPlayer slurpKiller = SlurpPlayerManager.getPlayer(killerEntity);
                if (slurpKiller == null) {
                    killerEntity.sendMessage(TextBuilder.error("You are not in a session, stop killing people!!!"));
                    return;
                }
                final int sips = SlurpConfig.getValue(ConfigValue.SIP_KILLER);
                final int shots = SlurpConfig.getValue(ConfigValue.SHOT_KILLER);
                SlurpEntryBuilder entry = new SlurpEntryBuilder(sips, shots, slurpKiller.getUuid(), slurpKiller.getSession().getUuid(),
                        false, false);
                ConsumableGivingHandler.serverGiveConsumable(killerEntity, entry);
                // Inform killed player
                Component text = Component.text("You have been killed by ", NamedTextColor.GRAY).append(Component.text(killerEntity.getName(), NamedTextColor.DARK_GRAY))
                        .append(Component.text(" and they have to take a shot!", NamedTextColor.GRAY));
                p.sendMessage(TextBuilder.info(text));
            }
        }
    }
}
