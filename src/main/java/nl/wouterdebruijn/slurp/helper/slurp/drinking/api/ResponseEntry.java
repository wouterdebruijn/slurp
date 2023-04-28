package nl.wouterdebruijn.slurp.helper.slurp.drinking.api;

import nl.wouterdebruijn.slurp.helper.slurp.drinking.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.slurp.drinking.manager.SlurpPlayerManager;

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
