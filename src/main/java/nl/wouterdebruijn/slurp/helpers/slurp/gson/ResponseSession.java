package nl.wouterdebruijn.slurp.helpers.slurp.gson;

import nl.wouterdebruijn.slurp.helpers.slurp.SlurpSession;

import java.util.Date;

public class ResponseSession {
    private String uuid;
    private String shortcode;
    private boolean active;
    private Date created;
    private Date updated;

    private String token;

    public SlurpSession toSlurpSession() {
        return new SlurpSession(shortcode, uuid, active, token);
    }
}
