package nl.wouterdebruijn.slurp.helper.game.handlers;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.exceptions.SlurpMessageException;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.manager.DrinkingBuddyManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpSessionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class ConsumableGivingHandler {
    public static String getTextSips(int sips) {
        return sips == 1 ? "sip" : "sips";
    }

    public static String getTextShots(int shots) {
        return shots == 1 ? "shot" : "shots";
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

    public static CompletableFuture<ArrayList<SlurpEntry>> serverGiveConsumable(Player target, SlurpEntryBuilder entry) {
        SlurpPlayer slurpTarget = SlurpPlayerManager.getPlayer(target);

        if (slurpTarget == null) {
            return CompletableFuture.failedFuture(new SlurpMessageException("You or the target are not in a session!"));
        }

        SlurpSession serverSession = SlurpSessionManager.getSession(slurpTarget.getSession().getUuid());

        if (serverSession == null) {
            return CompletableFuture.failedFuture(new SlurpMessageException("You or the target are not in a session!"));
        }
        return SlurpEntry.create(entry, serverSession.getToken());
    }

    public static void giveUnifiedSlurp(Player trigger, SlurpEntryBuilder entry) {
        SlurpPlayer triggerSP = SlurpPlayerManager.getPlayer(trigger);
        SlurpSession serverSesh = SlurpSessionManager.getSession(triggerSP.getSession().getUuid());
        if (serverSesh != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                SlurpPlayer sp = SlurpPlayerManager.getPlayer(p);
                if (sp == null) continue;
                SlurpEntry.createDirect(entry.copyForPlayer(sp), serverSesh.getToken());
            }
        } else {
            Slurp.logger.log(Level.SEVERE, "Could not retrieve session!");
        }
    }
}
