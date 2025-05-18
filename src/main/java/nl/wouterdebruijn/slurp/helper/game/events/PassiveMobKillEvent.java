package nl.wouterdebruijn.slurp.helper.game.events;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class PassiveMobKillEvent extends GameEvent implements Listener {
    private static final ArrayList<EntityType> entities = new ArrayList<>(Arrays.asList(
            EntityType.BAT,
            EntityType.BEE,
            EntityType.CAT,
            EntityType.CHICKEN,
            EntityType.COD,
            EntityType.COW,
            EntityType.DOLPHIN,
            EntityType.DONKEY,
            EntityType.FOX,
            EntityType.HORSE,
            EntityType.IRON_GOLEM,
            EntityType.LLAMA,
            EntityType.MULE,
            EntityType.OCELOT,
            EntityType.PANDA,
            EntityType.PARROT,
            EntityType.PIG,
            EntityType.POLAR_BEAR,
            EntityType.PUFFERFISH,
            EntityType.RABBIT,
            EntityType.SALMON,
            EntityType.SHEEP,
            EntityType.SQUID,
            EntityType.STRIDER,
            EntityType.TROPICAL_FISH,
            EntityType.TURTLE,
            EntityType.VILLAGER,
            EntityType.WANDERING_TRADER,
            EntityType.WOLF
    ));

    public PassiveMobKillEvent(FileConfiguration config) {
        super(config, "passive-mob-kill");
    }

    @Override
    public GameEvent register(Slurp plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        return this;
    }

    @EventHandler
    public void onEvent(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();

        // Check if the entity was killed by a player
        if (player == null)
            return;

        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(player, slurpPlayer)) {
            return;
        }

        // Check the entity type
        if (!entities.contains(event.getEntityType()))
            return;

        // Check if the player has a chance to trigger the event
        if (!this.chanceTrigger())
            return;

        // Trigger the event
        this.triggerFor(slurpPlayer);
    }
}
