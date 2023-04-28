package nl.wouterdebruijn.slurp.helper.game.api;

import nl.wouterdebruijn.slurp.exceptions.MissingSessionException;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;

import java.util.ArrayList;
import java.util.Date;

public class ResponseSessionPlayers {
    private String uuid;
    private Date created;
    private Date updated;
    private String shortcode;
    private boolean active;
    private ArrayList<ResponsePlayer> players;

    public ArrayList<SlurpPlayer> toSlurpPlayers() throws MissingSessionException {
        ArrayList<SlurpPlayer> slurpPlayers = new ArrayList<>();
        for (ResponsePlayer player : players) {
            slurpPlayers.add(player.toSlurpPlayer());
        }
        return slurpPlayers;
    }
}
