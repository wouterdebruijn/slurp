package nl.wouterdebruijn.slurp.entity;

import org.bukkit.entity.Player;

public class SlurpPlayer extends BaseEntry {
    private final Player minecraftPlayer;
    private final boolean isDrinkingBuddy;

    public SlurpPlayer(Player minecraftPlayer) {
        this.uuid = minecraftPlayer.getUniqueId();
        this.minecraftPlayer = minecraftPlayer;
        this.isDrinkingBuddy = false;
    }

    public Player getMinecraftPlayer() {
        return minecraftPlayer;
    }

    public boolean isDrinkingBuddy() {
        return isDrinkingBuddy;
    }
}
