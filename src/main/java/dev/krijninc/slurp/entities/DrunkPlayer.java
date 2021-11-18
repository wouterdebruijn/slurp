package dev.krijninc.slurp.entities;

import dev.krijninc.slurp.types.Consumables;
import dev.krijninc.slurp.Slurp;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DrunkPlayer {
    private final UUID uuid;

    public Consumables remaining = new Consumables();
    public Consumables taken = new Consumables();

    public boolean isDrinkingBuddy = false;
    public boolean isDrinking = true;

    public Player getBukkitPlayer() {
        return Slurp.getPlugin().getServer().getPlayer(this.uuid);
    }
    public UUID getUuid() { return this.uuid; }

    public DrunkPlayer(UUID uuid) {
        this.uuid = uuid;
    }
}
