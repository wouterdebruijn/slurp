package nl.wouterdebruijn.slurp.listener;

import nl.wouterdebruijn.slurp.helper.RandomGenerator;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.handlers.ConsumableGivingHandler;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerMovementListener implements Listener {
    // Chance of 1:1.000.000
    final int chance = 1000000; // Chance player has of stumbling
    final int toDrink = 2; // sips player has to drink
    final int effectDur = 8; // duration of potion effect in seconds
    final int effectAmpl = 1; // amplification of aforementioned potion effect.

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        SlurpPlayer sp = SlurpPlayerManager.getPlayer(p);
        if (sp == null) {
            p.sendMessage(TextBuilder.error("You are not in a session!"));
            return;
        }
        // If the player is sprinting, there is a chance he stumbles and falls. Stumbling means player will have to drink.
        if (p.isSprinting()) {
            boolean stumbles = RandomGenerator.hasChance(chance);
            if (stumbles) {
                SlurpEntryBuilder entry = new SlurpEntryBuilder(toDrink, 0, sp.getUuid(), sp.getSession().getUuid(), false, false);
                ConsumableGivingHandler.serverGiveConsumable(p, entry);
                p.addPotionEffect(PotionEffectType.SLOW.createEffect(effectDur, effectAmpl));
            }
        }
    }
}
