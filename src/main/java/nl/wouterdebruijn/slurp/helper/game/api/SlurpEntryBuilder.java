package nl.wouterdebruijn.slurp.helper.game.api;

import nl.wouterdebruijn.slurp.helper.game.entity.Consumable;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;

public class SlurpEntryBuilder {
    private int sips;
    private int shots;
    private String player;
    private String session;
    private boolean giveable;
    private boolean transfer;

    /**
     * Create entry to change slurp data
     *
     * @param sips Sips to add or remove. Positive values will add, negative values will subtract.
     * @param shots Shots to add or remove. Postive values will add, negative values will subtract.
     * @param player Player UUID whom the entry is meant for.
     * @param session UUID of the session of the player whom the entry is for.
     * @param giveable True if the sips can be given away, false if not.
     * @param transfer True if the sips are transferred to another player, false if not.
     */
    public SlurpEntryBuilder(int sips, int shots, String player, String session, boolean giveable, boolean transfer) {
        this.sips = sips;
        this.shots = shots;
        this.player = player;
        this.session = session;
        this.giveable = giveable;
        this.transfer = transfer;
    }

    public String getPlayerUuid() {
        return player;
    }

    public SlurpEntryBuilder copyForPlayer(SlurpPlayer player) {
        return new SlurpEntryBuilder(sips, shots, player.getUuid(), session, giveable, transfer);
    }

    public boolean shouldTransferToDrinkingBuddies() {
        return !transfer && !giveable;
    }

    public int getSips() {
        return sips;
    }

    public int getShots() {
        return shots;
    }

    public Consumable getTaken() {
        Consumable taken = new Consumable();

        if (this.giveable) {
            return taken;
        }

        if (shots < 0)
            taken.setShots(shots);

        if (sips < 0)
            taken.setSips(sips);

        return taken;
    }

    public Consumable getRemaining() {
        Consumable remaining = new Consumable();

        if (this.giveable) {
            return remaining;
        }

        remaining.setShots(shots);
        remaining.setSips(sips);

        return remaining;
    }

    public Consumable getGiveable() {
        Consumable giveable = new Consumable();

        if (!this.giveable) {
            return giveable;
        }

        giveable.setShots(shots);
        giveable.setSips(sips);

        return giveable;
    }
}
