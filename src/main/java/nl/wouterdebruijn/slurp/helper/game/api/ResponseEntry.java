package nl.wouterdebruijn.slurp.helper.game.api;

import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;

public class ResponseEntry {
    private String id;
    private int units;
    private String player;
    private boolean giveable;
    private boolean hide;

    public SlurpEntry toSlurpEntry() {
        return new SlurpEntry(id, units, SlurpPlayerManager.getPlayer(player), giveable, hide);
    }
}
