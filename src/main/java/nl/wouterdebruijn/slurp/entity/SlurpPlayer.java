package nl.wouterdebruijn.slurp.entity;

import nl.wouterdebruijn.slurp.Slurp;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SlurpPlayer {
    private final UUID uuid;
    public SlurpConsumable remaining = new SlurpConsumable();
    public SlurpConsumable taken = new SlurpConsumable();
    public SlurpConsumable giveable = new SlurpConsumable();

    public boolean isDrinkingBuddy = false;

    public SlurpPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getMinecraftPlayer() {
        return Slurp.getPlugin().getServer().getPlayer(uuid);
    }
}
