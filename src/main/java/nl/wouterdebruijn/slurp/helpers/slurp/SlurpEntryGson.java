package nl.wouterdebruijn.slurp.helpers.slurp;

public class SlurpEntryGson {
    private int sips;
    private int shots;

    private String player;
    private String session;
    private boolean giveable;
    private boolean transfer;

    public SlurpEntryGson(int sips, int shots, String player, String session, boolean giveable, boolean transfer) {
        this.sips = sips;
        this.shots = shots;
        this.player = player;
        this.session = session;
        this.giveable = giveable;
        this.transfer = transfer;
    }
}
