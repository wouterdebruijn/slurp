package nl.wouterdebruijn.slurp.helper.game.handlers;

import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.manager.DrinkingBuddyManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpSessionManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class ConsumableGivingHandler {
    public static String getTextSips(int sips) {
        return sips == 1 ? "sip" : "sips";
    }

    public static String getTextShots(int shots) {
        return shots == 1 ? "shot" : "shots";
    }

    public static void playerGiveConsumable(Player target, Player origin, SlurpEntryBuilder entry, Consumer<ArrayList<SlurpEntry>> callback) {
        SlurpPlayer slurpTarget = SlurpPlayerManager.getPlayer(target);
        SlurpPlayer slurpOrigin = SlurpPlayerManager.getPlayer(origin);

        int sips = entry.getSips();
        int shots = entry.getShots();

        if (slurpTarget == null || slurpOrigin == null) {
            origin.sendMessage(TextBuilder.error("You or the target are not in a session!"));
            return;
        }

        if (target == origin) {
            origin.sendMessage(TextBuilder.error("You can't give drinks to yourself!"));
            return;
        }

        ArrayList<SlurpPlayer> buddies = DrinkingBuddyManager.getBuddiesOfPlayer(slurpTarget);

        if (buddies != null && buddies.contains(slurpOrigin)) {
            origin.sendMessage(TextBuilder.error("You can't give drinks to your drinking buddies!"));
            return;
        }

        SlurpEntry.create(entry, slurpOrigin.getSession().getToken(), entries -> {
            if (sips > 0 && shots > 0) {
                origin.sendMessage(TextBuilder.success(String.format("You gave %s %d %s and %d %s!", target.getName(), sips, getTextSips(sips), shots, getTextShots(shots))));
                target.sendMessage(TextBuilder.success(String.format("%s gave you %d %s and %d %s!", origin.getName(), sips, getTextSips(sips), shots, getTextShots(shots))));
            }

            else if (sips > 0) {
                origin.sendMessage(TextBuilder.success(String.format("You gave %s %d %s!", target.getName(), sips, getTextSips(sips))));
                target.sendMessage(TextBuilder.success(String.format("%s gave you %d %s!", origin.getName(), sips, getTextSips(sips))));
            }

            else if (shots > 0) {
                origin.sendMessage(TextBuilder.success(String.format("You gave %s %d %s!", target.getName(), shots, getTextShots(shots))));
                target.sendMessage(TextBuilder.success(String.format("%s gave you %d %s!", origin.getName(), shots, getTextShots(shots))));
            }

            callback.accept(entries);
        });
    }

    public static void serverGiveConsumable(Player target, SlurpEntryBuilder entry, Consumer<ArrayList<SlurpEntry>> callback) {
        SlurpPlayer slurpTarget = SlurpPlayerManager.getPlayer(target);

        if (slurpTarget == null) {
            return;
        }

        SlurpSession session = SlurpSessionManager.getSession(slurpTarget.getSession().getUuid());

        if (session == null) {
            return;
        }

        String serverToken = session.getToken();

        SlurpEntry.create(entry, serverToken, callback);
    }
}
