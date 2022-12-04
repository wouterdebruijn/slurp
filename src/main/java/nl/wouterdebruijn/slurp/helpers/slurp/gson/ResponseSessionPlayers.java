package nl.wouterdebruijn.slurp.helpers.slurp.gson;

import nl.wouterdebruijn.slurp.helpers.slurp.SlurpPlayer;
import nl.wouterdebruijn.slurp.helpers.slurp.exceptions.MissingSessionException;

import java.util.ArrayList;
import java.util.Date;

public class ResponseSessionPlayers {
    private String uuid;
    private Date created;
    private Date updated;
    private String shortcode;
    private boolean active;
    private ArrayList<ResponsePlayer> players;

    public ResponseSessionPlayers() {
    }

    public ArrayList<SlurpPlayer> toSlurpPlayers() throws MissingSessionException {
        ArrayList<SlurpPlayer> slurpPlayers = new ArrayList<>();
        for (ResponsePlayer player : players) {
            slurpPlayers.add(player.toSlurpPlayer());
        }
        return slurpPlayers;
    }
}
