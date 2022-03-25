package nl.wouterdebruijn.slurp.repository;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.api.SlurpAPI;
import nl.wouterdebruijn.slurp.controller.*;
import nl.wouterdebruijn.slurp.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.exceptions.APIPostException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlurpEntryRepository {
    public static void cache(SlurpEntry entry, boolean recursiveDrinkingBuddies) {
        SlurpPlayer player = SlurpPlayerRepository.get(entry.getPlayer());

        int firstTakenShots = player.taken.shots;
        int firstTakenSips = player.taken.sips;

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

        int savezoneShotsMin = ConfigController.getInt("drinking-savezone.shots.min");
        int savezoneShotsMax = ConfigController.getInt("drinking-savezone.shots.max");
        int savezoneSipsMin = ConfigController.getInt("drinking-savezone.sips.min");
        int savezoneSipsMax = ConfigController.getInt("drinking-savezone.sips.max");

        int totalshots = player.taken.shots + player.remaining.shots;
        int totalsips = player.taken.sips + player.remaining.sips;

        Player mcPlayer = Slurp.getPlugin().getServer().getPlayer(player.getUuid());

        if (mcPlayer != null) {
            if (player.taken.shots < (totalshots / 100f) * savezoneShotsMin && totalshots > 3 && player.taken.shots <= firstTakenShots) {
                SmartTitleController.playTitle(mcPlayer, "\u00A7cYou are a slacker!", "\u00A7eTake some more shots!");
                MessageController.broadcast(true, String.format("%s is slacking on their shots!", mcPlayer.getName()));
                mcPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 0));
            } else
                mcPlayer.removePotionEffect(PotionEffectType.SLOW);

            if (player.taken.sips < (totalsips / 100f) * savezoneSipsMin && totalsips > 60 && player.taken.sips <= firstTakenSips) {
                SmartTitleController.playTitle(mcPlayer, "\u00A74This man is slacking!", "\u00A7eCatch up on those sips mydude!");
                MessageController.broadcast(true, String.format("%s is slacking on their sips!", mcPlayer.getName()));
                mcPlayer.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 20, 0));
            } else
                mcPlayer.removePotionEffect(PotionEffectType.CONFUSION);

            if (player.taken.shots > (totalshots / 100f) * savezoneShotsMax) {
                SmartTitleController.playTitle(mcPlayer, "\u00A72Total gamer!", "\u00A7eYou are drinking like a bender!");
                MessageController.broadcast(true, String.format("%s is tanking down those shots!", mcPlayer.getName()));
                mcPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 1));
            } else
                mcPlayer.removePotionEffect(PotionEffectType.SPEED);

            if (player.taken.sips > (totalsips / 100f) * savezoneSipsMax) {
                SmartTitleController.playTitle(mcPlayer, "\u00A7aNo lill' bitch!", "\u00A7eNo going back now!");
                MessageController.broadcast(true, String.format("%s is tanking down those sips!", mcPlayer.getName()));
                mcPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 9999999, 0));
            } else
                mcPlayer.removePotionEffect(PotionEffectType.REGENERATION);

        }

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
