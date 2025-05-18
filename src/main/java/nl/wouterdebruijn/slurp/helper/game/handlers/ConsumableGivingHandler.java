package nl.wouterdebruijn.slurp.helper.game.handlers;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import nl.wouterdebruijn.slurp.exceptions.SlurpMessageException;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.manager.DrinkingBuddyManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;

public class ConsumableGivingHandler {
    public static String getUnitText(int sips) {
        return sips == 1 ? "unit" : "units";
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
                    .success(String.format("You gave %s %d %s!", target.getName(), units, getUnitText(units))));
            target.sendMessage(TextBuilder
                    .success(String.format("%s gave you %d %s!", origin.getName(), units, getUnitText(units))));

            return CompletableFuture.completedFuture(entries);
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
    }
}
