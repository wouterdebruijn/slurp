package nl.wouterdebruijn.slurp.helpers.slurp;

import java.util.ArrayList;

public class SlurpSessionManager {
    public static ArrayList<SlurpSession> sessions = new ArrayList<>();

    public static SlurpSession getSession(String session) {
        for (SlurpSession slurpSession : sessions) {
            if (slurpSession.getUuid().equals(session)) {
                return slurpSession;
            }
        }
        return null;
    }

    public static void addSession(SlurpSession session) {
        sessions.add(session);
    }
}
