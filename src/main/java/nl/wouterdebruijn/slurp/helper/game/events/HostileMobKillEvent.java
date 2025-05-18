package nl.wouterdebruijn.slurp.helper.game.events;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;

public class HostileMobKillEvent extends GameEvent implements Listener {
    private static final ArrayList<EntityType> entities = new ArrayList<>(Arrays.asList(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.SPIDER,
            EntityType.CAVE_SPIDER,
            EntityType.CREEPER,
            EntityType.WITCH,
            EntityType.SLIME,
            EntityType.MAGMA_CUBE,
            EntityType.ENDERMAN,
            EntityType.ZOMBIFIED_PIGLIN,
            EntityType.SILVERFISH,
            EntityType.PHANTOM,
            EntityType.DROWNED,
            EntityType.HUSK,
            EntityType.STRAY,
            EntityType.WITHER_SKELETON,
            EntityType.EVOKER,
            EntityType.VEX,
            EntityType.PILLAGER,
            EntityType.RAVAGER,
            EntityType.VINDICATOR,
            EntityType.GUARDIAN,
            EntityType.ELDER_GUARDIAN,
            EntityType.SHULKER,
            EntityType.ENDERMITE,
            EntityType.GHAST,
            EntityType.BLAZE,
            EntityType.HOGLIN,
            EntityType.PIGLIN,
            EntityType.PIGLIN_BRUTE,
            EntityType.ZOGLIN,
            EntityType.WITHER,
            EntityType.ENDER_DRAGON,
            EntityType.GIANT,
            EntityType.ILLUSIONER));

    public HostileMobKillEvent(FileConfiguration config) {
        super(config, "hostile-mob-kill");
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
