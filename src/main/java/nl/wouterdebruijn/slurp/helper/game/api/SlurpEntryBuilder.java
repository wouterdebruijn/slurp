package nl.wouterdebruijn.slurp.helper.game.api;

import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;

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

    public void build() {
        SlurpEntry.create(this, session);
    }
}
