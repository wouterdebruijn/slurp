package dev.krijninc.slurp.entities;

import dev.krijninc.slurp.Consumables;
import dev.krijninc.slurp.Slurp;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DrunkPlayer {
    private UUID uuid;
    private UUID playerUUID;

    public Consumables remaining;
    public Consumables taken;
    public Consumables giveable;

    public boolean isDrinkingBuddy;
    public boolean isDrinking = true;

    public Player getBukkitPlayer() {
        return Slurp.getPlugin().getServer().getPlayer(this.playerUUID);
    }
}
