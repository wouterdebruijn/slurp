package nl.wouterdebruijn.slurp.helper.game.api;

import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;

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
