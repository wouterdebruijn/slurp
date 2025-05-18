package nl.wouterdebruijn.slurp.helper.game.manager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.endpoint.PocketBase;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;

public class SlurpSessionManager {

    private static final String API_URL = SlurpConfig.apiUrl();
    public static ArrayList<SlurpSession> sessions = new ArrayList<>();
    public static HashMap<SlurpSession, WebSocket> websockets = new HashMap<>();

    /**
     * Method needs to be refactored to use the new API
     */
    @Deprecated
    public static void subscribeToSession(SlurpSession session) {
        return;

        // if (websockets.containsKey(session)) {
        // // Don't subscribe if we're already subscribed
        // return;
        // }

        // URI websocketURI = URI.create((API_URL + "/socket/session/" +
        // session.getId()).replace("http", "ws"));

        // HttpClient client = HttpClient.newHttpClient();
        // WebSocket socket = client.newWebSocketBuilder()
        // .buildAsync(websocketURI, new SlurpSessionManager.WebSocketListener(session))
        // .join();

        // websockets.put(session, socket);
    }

    /**
     * Method needs to be refactored to use the new API
     */
    @Deprecated
    public static void unsubscribeFromSession(SlurpSession session) {
        return;

        // WebSocket socket = websockets.get(session);

        // if (session == null) {
        // return;
        // }

        // socket.sendClose(WebSocket.NORMAL_CLOSURE, "Bye")
        // .whenComplete((webSocket, throwable) -> {
        // if (throwable != null) {
        // Slurp.logger.log(Level.SEVERE, "Failed to close websocket connection");
        // throw new RuntimeException(throwable);
        // }
        // });

        // websockets.remove(session);
    }

    /**
     * Retrieves a SlurpSession by its ID.
     *
     * @param session the session ID to search for
     * @return the SlurpSession with the given ID, or null if not found
     */
    public static SlurpSession getSession(String session) {
        // Get session from cache
        for (SlurpSession slurpSession : sessions) {
            if (slurpSession.getId().equals(session)) {
                Slurp.logger.info("Loaded session from cache: " + slurpSession.getId());
                return slurpSession;
            }
        }

        // Query the API for the session
        try {
            SlurpSession slurpSession = PocketBase.getSession(session).get();
            addSession(slurpSession);

            Slurp.logger.info("Loaded session: " + slurpSession.getId());
            return slurpSession;
        } catch (Exception e) {
            Slurp.logger.log(Level.WARNING, "Failed to load session: " + session + " from API");
            Slurp.logger.log(Level.WARNING, e.getMessage());
            return null;
        }
    }

    public static ArrayList<SlurpSession> dump() {
        return sessions;
    }

    public static void saveToDisk() {
        try {
            Gson gson = new Gson();
            File file = new File(Slurp.plugin.getDataFolder().getAbsolutePath(), "sessions.json");
            file.getParentFile().mkdir();

            file.createNewFile();

            Writer writer = new FileWriter(file, false);
            gson.toJson(sessions, writer);

            writer.flush();
            writer.close();
            Slurp.logger.info("Saved sessions to disk");
        } catch (Exception e) {
            Slurp.logger.log(Level.SEVERE, "Failed to save sessions to disk");
            throw new RuntimeException(e);
        }
    }

    public static void loadFromDisk() {
        try {
            Gson gson = new Gson();
            File file = new File(Slurp.plugin.getDataFolder().getAbsolutePath(), "sessions.json");
            if (file.exists()) {
                Reader reader = new FileReader(file);

                // Restore the player list
                sessions = gson.fromJson(reader, new TypeToken<ArrayList<SlurpSession>>() {
                }.getType());

                // Dump sessions
                sessions.forEach(slurpSession -> {
                    Slurp.logger.info("Loaded session: " + slurpSession.getId());
                });

                Slurp.logger.info("Loaded sessions from disk");

                reader.close();
            }
        } catch (Exception e) {
            Slurp.logger.log(Level.SEVERE, "Failed to load sessions from disk");
            throw new RuntimeException(e);
        }
    }

    public static void addSession(SlurpSession session) {
        sessions.add(session);
        subscribeToSession(session);
    }

    public static void remove(SlurpSession session) {
        sessions.remove(session);
        unsubscribeFromSession(session);
    }

    private static class WebSocketListener implements WebSocket.Listener {
        SlurpSession session;

        public WebSocketListener(SlurpSession session) {
            this.session = session;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            Slurp.logger.info("Websocket opened");
            WebSocket.Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            Slurp.logger.info("Received websocket data: " + data.toString());

            // TODO: implement for Pocketbase

            // ArrayList<ResponsePlayer> responsePlayers = gson.fromJson(data.toString(),
            // new TypeToken<ArrayList<ResponsePlayer>>() {
            // }.getType());
            // responsePlayers.forEach(responsePlayer -> {
            // SlurpPlayer player = SlurpPlayerManager.getPlayer(responsePlayer.getId());

            // if (player == null) {
            // Slurp.logger.log(Level.WARNING,
            // "Received player update for unknown player: " + responsePlayer.getId());
            // return;
            // }

            // player.setGiveable(responsePlayer.getGiveable());
            // player.setTaken(responsePlayer.getTaken());
            // player.setRemaining(responsePlayer.getRemaining());
            // });

            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            Slurp.logger.warning("Websocket closed with status code " + statusCode + " and reason: " + reason);

            if (statusCode != WebSocket.NORMAL_CLOSURE) {
                // Resubscribe if the connection was closed unexpectedly
                SlurpSessionManager.subscribeToSession(session);
                Slurp.logger.info("Resubscribed to session " + session.getId());
            }

            return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
        }
    }
}
