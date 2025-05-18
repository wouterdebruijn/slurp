package nl.wouterdebruijn.slurp.helper.game.entity;

import java.util.concurrent.ExecutionException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import nl.wouterdebruijn.slurp.endpoint.PocketBase;
import nl.wouterdebruijn.slurp.exceptions.ApiResponseException;
import nl.wouterdebruijn.slurp.exceptions.ApiUrlException;
import nl.wouterdebruijn.slurp.exceptions.CreateSessionException;
import nl.wouterdebruijn.slurp.exceptions.MissingSessionException;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.filestorage.SlurpPlayerFileAdapter;
import nl.wouterdebruijn.slurp.helper.game.manager.ScoreboardManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.infra.Subject;

public class SlurpPlayer extends Subject {
    private static final String API_URL = SlurpConfig.apiUrl();
    private final String uuid;
    private final SlurpSession session;
    private final String username;

    private int taken;
    private int giveable;
    private int remaining;

    public SlurpPlayer(String uuid, SlurpSession session, String username) {
        this.uuid = uuid;
        this.session = session;
        this.username = username;
        this.taken = 0;
        this.giveable = 0;
        this.remaining = 0;
    }

    public SlurpPlayer(SlurpPlayerFileAdapter slurpPlayerFileAdapter) {
        this.uuid = slurpPlayerFileAdapter.getUuid();
        this.session = slurpPlayerFileAdapter.getSession();
        this.username = slurpPlayerFileAdapter.getUsername();
        this.taken = 0;
        this.giveable = 0;
        this.remaining = 0;
    }

    public static SlurpPlayer create(Player player, String sessionShort)
            throws ApiUrlException, CreateSessionException, MissingSessionException, ApiResponseException,
            ExecutionException, InterruptedException {
        SlurpPlayer slurpPlayer = PocketBase.createPlayer(player, sessionShort).get();
        SlurpPlayerManager.addPlayer(player, slurpPlayer);
        ScoreboardManager.playerScoreboard(slurpPlayer);
        return slurpPlayer;
    }

    public String getUuid() {
        return uuid;
    }

    public SlurpSession getSession() {
        return session;
    }

    /**
     * Get the username of the player
     *
     * @return The username of the player
     */
    public String getUsername() {
        return this.getPlayer().getName();
    }

    /**
     * Get the bukkit player matching this slurp player
     *
     * @return The bukkit player, or null if not found
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(this.username);
    }

    public int getTaken() {
        return taken;
    }

    public void setTaken(int taken) {
        this.taken = taken;
        this.notifyObservers();
    }

    public int getGiveable() {
        return giveable;
    }

    public void setGiveable(int giveable) {
        this.giveable = giveable;
        this.notifyObservers();
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
        this.notifyObservers();
    }

    public void update() {
        this.notifyObservers();
    }
}
