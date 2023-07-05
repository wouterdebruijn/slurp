package nl.wouterdebruijn.slurp.listener;

import nl.wouterdebruijn.slurp.helper.ConfigValue;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.handlers.ConsumableGivingHandler;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class PlayerKillAnimalListener implements Listener {

    @EventHandler
    public void onPlayerKill(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            if (e.getEntity().getKiller() != null) {
                Player p = e.getEntity().getKiller();
                SlurpPlayer sp = SlurpPlayerManager.getPlayer(p);
                if (sp == null) {
                    p.sendMessage(TextBuilder.error("You are not in a session!"));
                    return;
                }
                if (isPassive(e.getEntity())) {
                    final int sips = SlurpConfig.getValue(ConfigValue.SIP_KILLED_ANIMAL);
                    final int shots = SlurpConfig.getValue(ConfigValue.SHOT_KILLED_ANIMAL);
                    SlurpEntryBuilder entry = new SlurpEntryBuilder(sips, shots, sp.getUuid(), sp.getSession().getUuid(), false, false);
                    ConsumableGivingHandler.serverGiveConsumable(p, entry);
                }
            }
        }
    }

    private boolean isPassive(LivingEntity entity) {
        return switch (entity.getType()) {
            case ALLAY, AXOLOTL, BAT, CAMEL, CAT, DONKEY, FOX, FROG, HORSE, MUSHROOM_COW, MULE, OCELOT, PARROT, SKELETON_HORSE,
                    SNIFFER, STRIDER, TADPOLE, TURTLE, VILLAGER, WANDERING_TRADER, BEE, LLAMA, DOLPHIN, PANDA, POLAR_BEAR, TRADER_LLAMA, WOLF ->
                    true;
            default -> false;
        };
    }
}
