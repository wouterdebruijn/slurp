package nl.wouterdebruijn.slurp.helper.game.manager;

import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.events.DrinkingBuddyEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class DrinkingBuddyManager {
    static final HashMap<String, ArrayList<SlurpPlayer>> drinkingBuddies = new HashMap<>();
    static final ArrayList<DrinkingBuddyEvent> drinkingBuddyEvents = new ArrayList<>();

    public static void enableDrinkingBuddyEvent(SlurpSession session) {
        for (DrinkingBuddyEvent event : drinkingBuddyEvents) {
            if (event.getSessionId().equals(session.getUuid())) {
                event.enable();
                return;
            }
        }

        DrinkingBuddyEvent event = new DrinkingBuddyEvent(session);
        event.enable();
        drinkingBuddyEvents.add(event);
    }

    public static void disableDrinkingBuddyEvent(SlurpSession session) {
        for (DrinkingBuddyEvent event : drinkingBuddyEvents) {
            if (event.getSessionId().equals(session.getUuid())) {
                event.disable();
                drinkingBuddyEvents.remove(event);
                return;
            }
        }
    }

    public static void setDrinkingBuddies(SlurpSession session, ArrayList<SlurpPlayer> players) {
        drinkingBuddies.put(session.getUuid(), players);
    }

    public static ArrayList<SlurpPlayer> getDrinkingBuddies(SlurpSession session) {
        ArrayList<SlurpPlayer> buddies = drinkingBuddies.get(session.getUuid());
        if (buddies == null) {
            return new ArrayList<>();
        }
        return buddies;
    }

    public static void deleteDrinkingBuddies(SlurpSession session) {
        drinkingBuddies.remove(session.getUuid());
    }
}
