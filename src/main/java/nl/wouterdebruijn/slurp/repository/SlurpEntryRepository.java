package nl.wouterdebruijn.slurp.repository;

import nl.wouterdebruijn.slurp.api.SlurpAPI;
import nl.wouterdebruijn.slurp.controller.LogController;
import nl.wouterdebruijn.slurp.controller.SidebarController;
import nl.wouterdebruijn.slurp.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.exceptions.APIPostException;

public class SlurpEntryRepository {
    public static void cache(SlurpEntry entry) {
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
