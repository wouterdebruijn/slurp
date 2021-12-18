package nl.wouterdebruijn.slurp.entity;

import nl.wouterdebruijn.slurp.controller.ConfigController;

import java.util.UUID;

public class SlurpEntry {
    private final UUID player;
    public int sips;
    public int shots;
    public boolean transfer;
    public boolean giveable;

    public SlurpEntry(UUID player, double shots, double sips, boolean transfer, boolean giveable) {
        this.player = player;
        this.transfer = transfer;
        this.giveable = giveable;

        if (shots % 1.0 > 0) {
            this.sips = (int) (shots * ConfigController.getInt("shots-to-sips-multiplier") + sips);
        } else {
            this.sips = (int) sips;
        }
        this.shots = (int) shots;
    }

    public UUID getPlayer() {
        return player;
    }
}
