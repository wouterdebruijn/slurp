package nl.wouterdebruijn.slurp.helper.game.events;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.Consumable;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpSessionManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class GameEvent {
    static final String CONFIG_PATH = "game-events";
    private final String name;
    private boolean enabled;
    private int chance;
    private String message;
    private ArrayList<GameEventConsumable> consumables;

    protected GameEvent(FileConfiguration config, String name) {
        this.name = name;
        readConfig(config);
    }

    /**
     * Called on event registration, register event listeners here
     *
     * @param plugin The Slurp plugin instance
     */
    abstract public GameEvent register(Slurp plugin);

    /**
     * Reload the event applying the values from the config
     *
     * @param config The Slurp config to read from
     */
    public void reload(FileConfiguration config) {
        readConfig(config);
    }

    protected String getPath() {
        return String.format("%s.%s", CONFIG_PATH, name);
    }

    protected int getChance() {
        return chance;
    }

    /**
     * Read an event object from the config and apply the values to this object
     *
     * @param config The Slurp config to read from, event is searched for by eventnamename
     */
    protected void readConfig(FileConfiguration config) {
        if (!config.contains(getPath())) {
            Slurp.logger.warning(String.format("Could not find event %s in config.yml", name));
        }

        this.enabled = config.getBoolean(getPath() + ".enabled");
        this.chance = config.getInt(getPath() + ".chance");
        String messageString = config.isString(getPath() + ".message") ? config.getString(getPath() + ".message") : "";

        if (messageString == null) messageString = "";

        this.message = messageString;
        this.consumables = new ArrayList<>();

        // Config contains objects with enabled, chance, amount and target. Create a GameEventConsumable for each object
        List<?> list = config.getList(getPath() + ".consumables");

        if (list == null) {
            Slurp.logger.warning("Could not find consumables for event " + name);
            return;
        }

        for (Object object : list) {
            LinkedHashMap<?, ?> map = (LinkedHashMap<?, ?>) object;

            if (map == null) {
                Slurp.logger.warning("Invalid consumable for event " + name);
                continue;
            }

            try {
                String type = map.get("type").toString();
                int amount = Integer.parseInt(map.get("amount").toString());
                String target = map.get("target").toString();
                boolean giveable = Boolean.parseBoolean(map.get("giveable").toString());

                GameEventConsumable consumable = new GameEventConsumable(GameEventConsumable.ConsumableType.valueOf(type.toUpperCase()), amount, giveable, GameEventConsumable.ConsumableTarget.valueOf(target.toUpperCase()));
                consumables.add(consumable);
            } catch (NullPointerException e) {
                Slurp.logger.warning("Invalid consumable '" + list.indexOf(object) + "' for event " + name);
            }
        }
        Slurp.logger.info("Loaded event " + name + this.enabled);
    }

    /**
     * Trigger this event for a player
     *
     * @param player The player to trigger the event for
     */
    protected void triggerFor(SlurpPlayer player) {
        Slurp.logger.info("Triggering event " + name + " for player " + player.getPlayer().getName());
        if (!enabled) return;

        Player minecraftPlayer = player.getPlayer();
        Bukkit.broadcast(TextBuilder.warning(message.replaceAll("%player%", minecraftPlayer.getName())));

        ArrayList<CompletableFuture<?>> futures = new ArrayList<>();

        for (GameEventConsumable consumable : consumables) {
            Consumable toAdd = consumable.toConsumable();
            SlurpSession session = SlurpSessionManager.getSession(player.getSession().getUuid());

            if (session == null) {
                Slurp.logger.warning("Session is null for player " + player.getUuid() + " in GameEvent " + name);
                return;
            }

            switch (consumable.target) {
                case PLAYER -> {
                    SlurpEntryBuilder entry = new SlurpEntryBuilder(toAdd.getSips(), toAdd.getShots(), player.getUuid(), player.getSession().getUuid(), consumable.giveable, false);
                    futures.add(SlurpEntry.create(entry, session.getToken()));
                }
                case ALL -> {
                    for (SlurpPlayer slurpPlayer : session.getPlayers()) {
                        SlurpEntryBuilder entry = new SlurpEntryBuilder(toAdd.getSips(), toAdd.getShots(), slurpPlayer.getUuid(), slurpPlayer.getSession().getUuid(), consumable.giveable, false);
                        futures.add(SlurpEntry.createDirect(entry, session.getToken()));
                    }
                }
                case RANDOM -> {
                    SlurpPlayer randomPlayer = session.getRandomPlayersInSession(1).get(0);
                    SlurpEntryBuilder entry = new SlurpEntryBuilder(toAdd.getSips(), toAdd.getShots(), randomPlayer.getUuid(), randomPlayer.getSession().getUuid(), consumable.giveable, false);
                    futures.add(SlurpEntry.create(entry, session.getToken()));
                }
            }
        }

        CompletableFuture[] futureArray = new CompletableFuture[futures.size()];
        futures.toArray(futureArray);

        // Wait for all futures to complete, creating all entries
        CompletableFuture.allOf(futureArray).join();
    }

    protected record GameEventConsumable(GameEvent.GameEventConsumable.ConsumableType type, int amount, boolean giveable,
                                         GameEvent.GameEventConsumable.ConsumableTarget target) {
        public Consumable toConsumable() {
            Consumable consumable = new Consumable();
            consumable.setShots(type == ConsumableType.SHOT ? amount : 0);
            consumable.setSips(type == ConsumableType.SIP ? amount : 0);
            return consumable;
        }

        private enum ConsumableType {
            SIP,
            SHOT
        }

        private enum ConsumableTarget {
            PLAYER,
            ALL,
            RANDOM
        }
    }
}
