package nl.wouterdebruijn.slurp.helper.game.manager;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.events.DrinkingBuddyEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class DrinkingBuddyManager {
    static final HashMap<String, ArrayList<SlurpPlayer>> drinkingBuddies = new HashMap<>();
    static final ArrayList<DrinkingBuddyEvent> drinkingBuddyEvents = new ArrayList<>();

    public static void enableDrinkingBuddyEvent(SlurpSession session) {
        // Check if event is already enabled
        for (DrinkingBuddyEvent event : drinkingBuddyEvents) {
            if (event.getSessionId().equals(session.getUuid())) {
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
                deleteDrinkingBuddies(session);
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

    /**
     * Return the list of drinking buddies of the given player, excluding the player itself.
     */
    public static ArrayList<SlurpPlayer> getBuddiesOfPlayer(SlurpPlayer player) {
        // Get drinking buddies and create copy of the list.
        ArrayList<SlurpPlayer> buddies = new ArrayList<>(getDrinkingBuddies(player.getSession()));

        for (SlurpPlayer buddy : buddies) {
            if (buddy.getUuid().equals(player.getUuid())) {
                // Remove the player itself from the list
                buddies.remove(buddy);
                return buddies;
            }
        }
        return null;
    }
}
