package nl.wouterdebruijn.slurp.helper.game.manager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.Future;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import cloud.prefab.sse.SSEHandler;
import cloud.prefab.sse.events.CommentEvent;
import cloud.prefab.sse.events.DataEvent;
import cloud.prefab.sse.events.Event;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.endpoint.PocketBase;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;

public class SlurpSessionManager {

    private static final String API_URL = SlurpConfig.apiUrl();
    public static ArrayList<SlurpSession> sessions = new ArrayList<>();

    public static Future<?> future = null;

    public static void addSession(SlurpSession session) {
        sessions.add(session);
    }

    public static void removeSession(SlurpSession session) {
        sessions.remove(session);
    }

    /**
     * SSE Flow subscriber, handles SSE events from pocketbase
     */
    private static class FlowSubscriber implements Flow.Subscriber<Event> {
        private String clientId;
        Subscription subscription;

        @Override
        public void onSubscribe(Subscription subscription) {
            Slurp.logger.info("Subscribed to realtime events (SSE)");
            subscription.request(1);

            this.subscription = subscription;
        }

        @Override
        public void onNext(Event item) {
            if (item instanceof CommentEvent) {
                CommentEvent commentEvent = (CommentEvent) item;
                Slurp.logger.info("Received SSE comment: " + commentEvent.getComment());
                // Handle the comment
            } else if (item instanceof DataEvent) {
                DataEvent dataEvent = (DataEvent) item;

                String eventName = dataEvent.getEventName();
                String dataString = dataEvent.getData();

                // Handle the event
                // You can parse the data and update the session list accordingly

                JsonObject jsonObject = JsonParser.parseString(dataString.trim()).getAsJsonObject();

                if (eventName.equals("PB_CONNECT")) {
                    // Connected to server, register clientId
                    clientId = jsonObject.get("clientId").getAsString();

                    ArrayList<String> subscriptions = new ArrayList<String>();
                    subscriptions.add("player_entries");

                    try {
                        PocketBase.registerSubscriptions(clientId, subscriptions);

                        subscription.request(Long.MAX_VALUE);
                    } catch (Exception e) {
                        Slurp.logger.severe("Failed to register subscriptions, empty connection");
                        Slurp.logger.severe(e.getMessage());
                    }
                }

                if (eventName.equals("player_entries")) {
                    SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(jsonObject.get("id").getAsString());

                    if (slurpPlayer == null) {
                        Slurp.logger.warning("Received unknown player: " + jsonObject.get("id").getAsString() + " " +
                                jsonObject.get("username").getAsString());
                        return;
                    }

                    int giveable = jsonObject.get("giveable").getAsInt();
                    int taken = jsonObject.get("taken").getAsInt() * -1;
                    int received = jsonObject.get("received").getAsInt();

                    slurpPlayer.setGiveable(giveable);
                    slurpPlayer.setTaken(jsonObject.get("taken").getAsInt());
                    slurpPlayer.setRemaining(received - taken);
                }

            } else {
                Slurp.logger.warning("Received unknown SSE event: " + item);
            }
        }

        @Override
        public void onError(Throwable throwable) {
            Slurp.logger.severe("Error in SSE subscription: " + throwable.getMessage());
            // Handle error

            Slurp.plugin.getServer().getScheduler().runTaskLaterAsynchronously(Slurp.plugin,
                    () -> {
                        try {
                            SlurpSessionManager.subscribe();
                        } catch (Exception e) {
                            Slurp.logger.severe("Failed to resubscribe to SSE: " + e.getMessage());
                        }
                    }, 20 * 5); // Retry after 5 seconds
        }

        @Override
        public void onComplete() {
            // Sessions should not complete?
            Slurp.logger.info("SSE subscription completed, restarting...");
            SlurpSessionManager.subscribe();
        }
    }

    public static HttpRequest subscribe() {
        // Subscribe to session using SSE
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest sseRequest = HttpRequest.newBuilder()
                .uri(java.net.URI.create(API_URL + "/api/realtime"))
                .header("Authorization", "Bearer " + SlurpConfig.getToken())
                .header("Accept", "text/event-stream")
                .build();

        SSEHandler sseHandler = new SSEHandler();

        FlowSubscriber flowSubscriber = new FlowSubscriber();

        sseHandler.subscribe(flowSubscriber);

        SlurpSessionManager.future = httpClient.sendAsync(sseRequest,
                HttpResponse.BodyHandlers.fromLineSubscriber(sseHandler))
                .handle((ignored, throwable) -> {
                    if (throwable != null) {
                        Slurp.logger.severe("Failed to subscribe to realtime SSE");
                        throw new RuntimeException(throwable);
                    }
                    return null;
                });

        return sseRequest;
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
}
