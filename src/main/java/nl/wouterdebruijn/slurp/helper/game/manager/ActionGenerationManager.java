package nl.wouterdebruijn.slurp.helper.game.manager;

import java.util.HashMap;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.events.AIHandlerEvent;

public class ActionGenerationManager {
    private static final HashMap<SlurpSession, AIHandlerEvent> handlers = new HashMap<>();

    public static void ensureHandler(SlurpSession session) {
        if (!handlers.containsKey(session)) {
            addHandler(session, new AIHandlerEvent(session));
            Slurp.logger.info("Created new AI handler for session: " + session.getShortcode());
        }
    }

    public static void addHandler(SlurpSession session, AIHandlerEvent handler) {
        handlers.put(session, handler);
        handler.schedule();
    }

    public static AIHandlerEvent getHandler(SlurpSession session) {
        return handlers.get(session);
    }

    public static void removeHandler(SlurpSession session) {
        handlers.remove(session).cancel();
    }

    public static void restartAIHandlerTask(SlurpSession session) {
        AIHandlerEvent handler = getHandler(session);
        if (handler != null) {
            handler.cancel();
            handler.schedule();
            Slurp.logger.info("Restarted AI handler for session: " + session.getShortcode());
        } else {
            Slurp.logger.warning("No AI handler found for session: " + session.getShortcode());
        }
    }
}
