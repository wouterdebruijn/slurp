package nl.wouterdebruijn.slurp.helper.game.filestorage;

import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;

public class SlurpPlayerFileAdapter extends SlurpPlayer {
    private String minecraftPlayerUuid;

    public SlurpPlayerFileAdapter(SlurpPlayer player, String minecraftUuid) {
        super(player.getUuid(), player.getSession(), player.getUsername());
        minecraftPlayerUuid = minecraftUuid;
    }

    public String getMinecraftPlayerUuid() {
        return minecraftPlayerUuid;
    }

}
