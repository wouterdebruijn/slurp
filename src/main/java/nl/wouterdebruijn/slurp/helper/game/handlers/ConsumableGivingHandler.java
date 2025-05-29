package nl.wouterdebruijn.slurp.helper.game.handlers;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import nl.wouterdebruijn.slurp.exceptions.SlurpMessageException;
import nl.wouterdebruijn.slurp.helper.ConfigValue;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.manager.DrinkingBuddyManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;

public class ConsumableGivingHandler {
    public static String getUnitText(int units) {
        int sipShotRatio = SlurpConfig.getValue(ConfigValue.SIP_SHOT_RATIO);

        // Display text for x shots and y sips
        int shots = units / sipShotRatio;
        int sips = units % sipShotRatio;

        StringBuilder text = new StringBuilder();

        if (shots > 0) {
            text.append(shots).append(" shot").append(shots > 1 ? "s" : "");
        }

        if (sips > 0) {
            if (text.length() > 0) {
                text.append(" and ");
            }
            text.append(sips).append(" sip").append(sips > 1 ? "s" : "");
        }

        return text.toString();
    }

    public static Component getConsumableText(int units) {
        return Component
                .text(String.format("%d %s", units, getUnitText(units)));
    }

    public static CompletableFuture<ArrayList<SlurpEntry>> playerGiveConsumable(Player target, Player origin,
            SlurpEntryBuilder entry) {
        SlurpPlayer slurpTarget = SlurpPlayerManager.getPlayer(target);
        SlurpPlayer slurpOrigin = SlurpPlayerManager.getPlayer(origin);

        int units = entry.getUnits();

        if (slurpTarget == null || slurpOrigin == null) {
            return CompletableFuture.failedFuture(new SlurpMessageException("You or the target are not in a session!"));
        }

        if (target == origin) {
            return CompletableFuture.failedFuture(new SlurpMessageException("You can't give drinks to yourself!"));
        }

        ArrayList<SlurpPlayer> buddies = DrinkingBuddyManager.getBuddiesOfPlayer(slurpTarget);

        if (buddies != null && buddies.contains(slurpOrigin)) {
            return CompletableFuture
                    .failedFuture(new SlurpMessageException("You can't give drinks to your drinking buddies!"));
        }

        CompletableFuture<ArrayList<SlurpEntry>> futureEntries = SlurpEntry.create(entry);

        try {
            ArrayList<SlurpEntry> entries = futureEntries.get();

            origin.sendMessage(TextBuilder
                    .success(String.format("You gave %s %s!", target.getName(),
                            getUnitText(units))));
            target.sendMessage(TextBuilder
                    .success(String.format("%s gave you %s!", origin.getName(),
                            getUnitText(units))));

            return CompletableFuture.completedFuture(entries);
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
    }
}
