package dev.krijninc.slurp.entities;

import dev.krijninc.slurp.Slurp;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class DrunkPlayer implements Player {
    private UUID uuid;
    private UUID playerUUID;

    public int remainingSips;
    public int remainingShots;
    public int takenShots;
    public int takenSips;

    public boolean isDrinkingBuddy;
    public boolean isDrinking = true;

    public Player getBukkitPlayer() {
        return Slurp.getPlugin().getServer().getPlayer(this.playerUUID);
    }
}
