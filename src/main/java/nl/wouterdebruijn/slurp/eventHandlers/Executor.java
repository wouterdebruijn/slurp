package nl.wouterdebruijn.slurp.eventHandlers;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.exceptions.APIPostException;
import nl.wouterdebruijn.slurp.repository.SlurpEntryRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.UUID;

abstract public class Executor<BukkitEvent extends Event> {
    public void execute(BukkitEvent event) {
        if (beforeExecution(event))
            onExecution(event);
    }

    abstract protected void onExecution(BukkitEvent event);

    protected boolean beforeExecution(BukkitEvent event) {
        return true;
    }

    protected SlurpEntry serverDrinkingEvent(int shots, int sips, boolean giveable) {
        ArrayList<SlurpEntry> entries = new ArrayList<>();

        for (Player player : Slurp.getPlugin().getServer().getOnlinePlayers()) {
            SlurpEntry entry = new SlurpEntry(player.getUniqueId(), shots, sips, false, giveable);
            SlurpEntryRepository.cache(entry, false);
            entries.add(entry);
        }

        // Update the server.
        Bukkit.getScheduler().runTaskAsynchronously(Slurp.getPlugin(), () -> {
            try {
                for (SlurpEntry entry : entries) {
                    SlurpEntryRepository.save(entry);
                }
            } catch (APIPostException e) {
                e.printStackTrace();
            }
        });

        return entries.get(0);
    }

    protected SlurpEntry playerDrinkEvent(UUID playerUUID, int shots, int sips, boolean giveable) {
        SlurpEntry entry = new SlurpEntry(playerUUID, shots , sips, false, giveable);
        SlurpEntryRepository.cache(entry, !giveable);

        Bukkit.getScheduler().runTaskAsynchronously(Slurp.getPlugin(), () -> {
            try {
                SlurpEntryRepository.save(entry);
            } catch (APIPostException e) {
                e.printStackTrace();
            }
        });

        return entry;
    }
}
