package nl.wouterdebruijn.slurp.helper.game.handlers;

import net.kyori.adventure.text.Component;
import nl.wouterdebruijn.slurp.exceptions.SlurpMessageException;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.manager.DrinkingBuddyManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ConsumableGivingHandler {
    public static String getTextSips(int sips) {
        return sips == 1 ? "sip" : "sips";
    }

    public static String getTextShots(int shots) {
        return shots == 1 ? "shot" : "shots";
    }

    public static Component getConsumableText(int sips, int shots) {
        if (sips > 0 && shots > 0) {
            return Component.text(String.format("%d %s and %d %s", sips, getTextSips(sips), shots, getTextShots(shots)));
        } else if (sips > 0) {
            return Component.text(String.format("%d %s", sips, getTextSips(sips)));
        } else if (shots > 0) {
            return Component.text(String.format("%d %s", shots, getTextShots(shots)));
        } else {
            return Component.text("nothing");
        }
    }

    public static CompletableFuture<ArrayList<SlurpEntry>> playerGiveConsumable(Player target, Player origin, SlurpEntryBuilder entry) {
        SlurpPlayer slurpTarget = SlurpPlayerManager.getPlayer(target);
        SlurpPlayer slurpOrigin = SlurpPlayerManager.getPlayer(origin);

        int sips = entry.getSips();
        int shots = entry.getShots();

        if (slurpTarget == null || slurpOrigin == null) {
            return CompletableFuture.failedFuture(new SlurpMessageException("You or the target are not in a session!"));
        }

        if (target == origin) {
            return CompletableFuture.failedFuture(new SlurpMessageException("You can't give drinks to yourself!"));
        }

        ArrayList<SlurpPlayer> buddies = DrinkingBuddyManager.getBuddiesOfPlayer(slurpTarget);

        if (buddies != null && buddies.contains(slurpOrigin)) {
            return CompletableFuture.failedFuture(new SlurpMessageException("You can't give drinks to your drinking buddies!"));
        }

        CompletableFuture<ArrayList<SlurpEntry>> futureEntries = SlurpEntry.create(entry, slurpOrigin.getSession().getToken());

        try {
            ArrayList<SlurpEntry> entries = futureEntries.get();

            if (sips > 0 && shots > 0) {
                origin.sendMessage(TextBuilder.success(String.format("You gave %s %d %s and %d %s!", target.getName(), sips, getTextSips(sips), shots, getTextShots(shots))));
                target.sendMessage(TextBuilder.success(String.format("%s gave you %d %s and %d %s!", origin.getName(), sips, getTextSips(sips), shots, getTextShots(shots))));
            } else if (sips > 0) {
                origin.sendMessage(TextBuilder.success(String.format("You gave %s %d %s!", target.getName(), sips, getTextSips(sips))));
                target.sendMessage(TextBuilder.success(String.format("%s gave you %d %s!", origin.getName(), sips, getTextSips(sips))));
            } else if (shots > 0) {
                origin.sendMessage(TextBuilder.success(String.format("You gave %s %d %s!", target.getName(), shots, getTextShots(shots))));
                target.sendMessage(TextBuilder.success(String.format("%s gave you %d %s!", origin.getName(), shots, getTextShots(shots))));
            }

            return CompletableFuture.completedFuture(entries);
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
    }
}
