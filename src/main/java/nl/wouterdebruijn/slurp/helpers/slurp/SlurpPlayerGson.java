package nl.wouterdebruijn.slurp.helpers.slurp;

public class SlurpPlayerGson extends SlurpPlayer {
    private String minecraftPlayerUuid;

    public SlurpPlayerGson(SlurpPlayer player, String minecraftUuid) {
        super(player.getUuid(), player.getSession(), player.getUsername());
        minecraftPlayerUuid = minecraftUuid;
    }

}
