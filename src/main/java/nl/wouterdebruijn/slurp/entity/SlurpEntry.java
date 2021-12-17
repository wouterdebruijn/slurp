package nl.wouterdebruijn.slurp.entity;

import org.bukkit.entity.Player;

public class SlurpEntry extends BaseEntry {
    final private int sips;
    final private int shots;
    final private boolean transfer;
    final private boolean giveable;
    final private Player player;

    public SlurpEntry(Player player, int sips, int shots, boolean transfer, boolean giveable) {
        this.player = player;
        this.sips = sips;
        this.shots = shots;
        this.transfer = transfer;
        this.giveable = giveable;
    }

    public int getSips() {
        return sips;
    }

    public int getShots() {
        return shots;
    }

    public boolean isTransfer() {
        return transfer;
    }

    public boolean isGiveable() {
        return giveable;
    }

    public Player getPlayer() {
        return player;
    }
}
