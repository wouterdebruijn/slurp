package nl.wouterdebruijn.slurp.helpers.slurp.gson;

import nl.wouterdebruijn.slurp.helpers.slurp.SlurpEntry;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpPlayer;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpPlayerManager;

public class ResponseEntry {
    private String uuid;
    private int sips;
    private int shots;
    private String player;
    private boolean giveable;
    private boolean transfer;

    public SlurpEntry toSlurpEntry() {
        return new SlurpEntry(uuid, sips, shots, SlurpPlayerManager.getPlayer(player), giveable, transfer);
    }
}
