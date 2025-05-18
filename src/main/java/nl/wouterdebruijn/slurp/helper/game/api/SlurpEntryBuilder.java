package nl.wouterdebruijn.slurp.helper.game.api;

import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;

public class SlurpEntryBuilder {
    private final int units;
    private final String player;
    private final String session;
    private final boolean giveable;
    private final boolean transfer;

    /**
     * Create entry to change slurp data
     *
     * @param sips     Sips to add or remove. Positive values will add, negative values will subtract.
     * @param shots    Shots to add or remove. Postive values will add, negative values will subtract.
     * @param player   Player UUID whom the entry is meant for.
     * @param session  UUID of the session of the player whom the entry is for.
     * @param giveable True if the sips can be given away, false if not.
     * @param transfer True if the sips are transferred to another player, false if not.
     */
    public SlurpEntryBuilder(int units, String player, String session, boolean giveable, boolean transfer) {
        this.units = units;
        this.player = player;
        this.session = session;
        this.giveable = giveable;
        this.transfer = transfer;
    }

    public String getPlayerUuid() {
        return player;
    }

    public SlurpEntryBuilder copyForPlayer(SlurpPlayer player) {
        return new SlurpEntryBuilder(units, player.getUuid(), session, giveable, transfer);
    }

    public boolean shouldTransferToDrinkingBuddies() {
        return !transfer && !giveable;
    }

    public int getUnits() {
        return units;
    }

    public int getTaken() {
        if (this.giveable || this.transfer) {
            return 0;
        }

        return units;
    }

    public int getRemaining() {
        if (this.giveable) {
            return 0;
        }

        return units;
    }

    public int getGiveable() {
        if (!this.giveable) {
            return 0;
        }

        return units;
    }
}
