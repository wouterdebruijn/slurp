package nl.wouterdebruijn.slurp.helpers.slurp;

public class SlurpEntry {
    private String uuid;
    private int sips;
    private int shots;

    private SlurpPlayer player;
    private boolean giveable;
    private boolean transfer;

    public SlurpEntry(String uuid, int sips, int shots, SlurpPlayer player, boolean giveable, boolean transfer) {
        this.uuid = uuid;
        this.sips = sips;
        this.shots = shots;
        this.player = player;
        this.giveable = giveable;
        this.transfer = transfer;
    }
}
