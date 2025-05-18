package nl.wouterdebruijn.slurp.helper.game.api;

import java.util.Date;

import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;

public class ResponseSession {
    private String id;
    private String shortcode;
    private boolean active;
    private Date created;
    private Date updated;

    public SlurpSession toSlurpSession() {
        return new SlurpSession(shortcode, id, active);
    }
}
