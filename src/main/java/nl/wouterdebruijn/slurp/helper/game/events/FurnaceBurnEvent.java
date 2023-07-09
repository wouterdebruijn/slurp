package nl.wouterdebruijn.slurp.helper.game.events;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceExtractEvent;

public class FurnaceBurnEvent extends GameEvent implements Listener {
    public FurnaceBurnEvent(FileConfiguration config) {
        super(config, "furnace-burn");
    }

    @Override
    public GameEvent register(Slurp plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        return this;
    }

    @EventHandler
    public void onEvent(FurnaceExtractEvent event) {
        Player player = event.getPlayer();
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        int amount = event.getItemAmount();

        if (SlurpPlayerManager.checkNull(player, slurpPlayer)) {
            return;
        }

        // The change is divided by the amount of items, so that the chance is the same for every item in the stack
        if (!chanceTrigger(amount))
            return;

        this.triggerFor(slurpPlayer);
    }
}
