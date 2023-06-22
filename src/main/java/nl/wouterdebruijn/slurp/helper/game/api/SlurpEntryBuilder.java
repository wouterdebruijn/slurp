package nl.wouterdebruijn.slurp.helper.game.api;

import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;

public class SlurpEntryBuilder {
    private int sips;
    private int shots;
    private String player;
    private String session;
    private boolean giveable;
    private boolean transfer;

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
}
