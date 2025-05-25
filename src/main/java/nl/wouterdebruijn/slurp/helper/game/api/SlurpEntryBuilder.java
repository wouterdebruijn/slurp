package nl.wouterdebruijn.slurp.helper.game.api;

import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;

public class SlurpEntryBuilder {
    private final int units;
    private final String player;
    private final String session;
    private final boolean giveable;
    private final boolean hide;

    /**
     * Create entry to change slurp data
     *
     * @param sips     Sips to add or remove. Positive values will add, negative
     *                 values will subtract.
     * @param shots    Shots to add or remove. Postive values will add, negative
     *                 values will subtract.
     * @param player   Player UUID whom the entry is meant for.
     * @param session  UUID of the session of the player whom the entry is for.
     * @param giveable True if the sips can be given away, false if not.
     * @param hide     Hide from calc and graph.
     */
    public SlurpEntryBuilder(int units, String player, String session, boolean giveable, boolean hide) {
        this.units = units;
        this.player = player;
        this.session = session;
        this.giveable = giveable;
        this.hide = hide;
    }

    public String getPlayerId() {
        return player;
    }

    public SlurpEntryBuilder copyForPlayer(SlurpPlayer player) {
        return new SlurpEntryBuilder(units, player.getId(), session, giveable, hide);
    }

    public boolean shouldTransferToDrinkingBuddies() {
        return !hide && !giveable;
    }

    public int getUnits() {
        return units;
    }

    public int getTaken() {
        if (this.giveable || this.hide || this.units > 0) {
            return 0;
        }

        return units;
    }

    public int getRemaining() {
        if (this.giveable || this.units < 0) {
            return 0;
        }

        return units;
    }

    public int getGiveable() {
        if (!this.giveable || this.units < 0) {
            return 0;
        }

        return units;
    }
}
