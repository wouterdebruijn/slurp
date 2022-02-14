package nl.wouterdebruijn.slurp.repository;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.api.SlurpAPI;
import nl.wouterdebruijn.slurp.controller.LogController;
import nl.wouterdebruijn.slurp.controller.MessageController;
import nl.wouterdebruijn.slurp.controller.SidebarController;
import nl.wouterdebruijn.slurp.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.exceptions.APIPostException;
import org.bukkit.Bukkit;

public class SlurpEntryRepository {
    public static void cache(SlurpEntry entry, boolean recursiveDrinkingBuddies) {
        SlurpPlayer player = SlurpPlayerRepository.get(entry.getPlayer());

        if (entry.giveable) {
            player.giveable.shots += entry.shots;
            player.giveable.sips += entry.sips;
        } else {
            player.remaining.shots += entry.shots;
            player.remaining.sips += entry.sips;

            if (!entry.transfer) {
                if (entry.shots < 0)
                    player.taken.shots -= entry.shots;
                if (entry.sips < 0)
                    player.taken.sips -= entry.sips;
            }
        }

        SlurpPlayerRepository.put(player);
        SidebarController.createSidebar(player);

        if (recursiveDrinkingBuddies && player.isDrinkingBuddy) {
            for (SlurpPlayer buddy : SlurpPlayerRepository.getDrinkingBuddies()) {
                if (buddy.getUuid() != player.getUuid()) {
                    SlurpEntry buddyEntry = new SlurpEntry(buddy.getUuid(), entry.shots, entry.sips, entry.transfer, entry.giveable);
                    cache(buddyEntry, false);

                    if (buddy.getMinecraftPlayer() != null)
                        MessageController.broadcast(true, "Their drinking buddy ", buddy.getMinecraftPlayer().getName(), " drinks as well!");

                    Bukkit.getScheduler().runTaskAsynchronously(Slurp.getPlugin(), () -> {
                        try {
                            SlurpEntryRepository.save(buddyEntry);
                        } catch (APIPostException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }

    public static void save(SlurpEntry entry) throws APIPostException {
        try {
            SlurpAPI.post("/entry", entry);
        } catch (Exception e) {
            LogController.error("Could not save Slurp Entry!");
            e.printStackTrace();
            throw new APIPostException();
        }
    }
}
