package nl.wouterdebruijn.slurp.endpoint;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Sound;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.ConfigValue;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.EventLog;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.manager.ActionGenerationManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;

public class GoogleAI {
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-04-17:generateContent";

    private String token;

    private GoogleAI(String token) {
        this.token = token;
    }

    private static GoogleAI instance;

    public static GoogleAI getInstance() {
        return instance;
    }

    public static void initialize(String token) {
        instance = new GoogleAI(token);
    }

    public static void generateActions(ArrayList<EventLog> eventLogs, SlurpSession session) {
        JsonArray jsonArray = new JsonArray();

        if (instance == null) {
            Slurp.logger.severe("GoogleAI instance is not initialized.");
            return;
        }

        for (EventLog eventLog : eventLogs) {
            jsonArray.add(eventLog.toJson());
        }

        GoogleAIRequest request = new GoogleAIRequest(jsonArray.toString());

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(
                            API_URL + "?key=" + instance.token))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(request.toJson().toString()))
                    .build();

            eventLogs.clear();

            HttpClient client = HttpClient.newHttpClient();

            Slurp.logger.info("Sending request to Google AI with " + jsonArray.size() + " events.");

            client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
                        String jsonString = jsonResponse.getAsJsonArray("candidates").get(0).getAsJsonObject()
                                .getAsJsonObject("content").getAsJsonArray("parts").get(0)
                                .getAsJsonObject().get("text").getAsString();

                        JsonObject jsonActions = JsonParser.parseString(jsonString).getAsJsonObject();
                        JsonArray actions = jsonActions.getAsJsonArray("actions");

                        int allowedActions = 0;

                        for (int i = 0; i < actions.size(); i++) {
                            JsonObject action = actions.get(i).getAsJsonObject();
                            if (action.get("amount").getAsInt() > 0 && action.get("rarity").getAsInt() >= SlurpConfig
                                    .getValue(ConfigValue.MINIMAL_RARITY)) {
                                allowedActions++;
                            }
                        }

                        int inTime = SlurpConfig.getValue(ConfigValue.GENERATION_INTERVAL) / (allowedActions + 1);
                        Slurp.logger.info("Total actions " + actions.size() + ", allowed actions: " + allowedActions);
                        Slurp.logger.info("Interval time: " + inTime / 60 + " minutes, " + inTime % 60
                                + " seconds per action.");

                        int processedActions = 0;

                        for (int i = 0; i < actions.size(); i++) {
                            final JsonObject action = actions.get(i).getAsJsonObject();
                            String target = action.get("target").getAsString();
                            int amount = action.get("amount").getAsInt()
                                    * SlurpConfig.getValue(ConfigValue.DIFFICULTY_MULTIPLIER);
                            boolean giveable = action.get("giveable").getAsBoolean();
                            String message = action.get("message").getAsString();
                            String type = action.get("type").getAsString();
                            int rarity = action.get("rarity").getAsInt();

                            if (amount <= 0 || rarity < SlurpConfig.getValue(ConfigValue.MINIMAL_RARITY)) {
                                Slurp.logger.info("Skipped: \"" + message + "\" with amount "
                                        + action.get("amount").getAsInt() + " and rarity "
                                        + action.get("rarity").getAsInt());

                                continue;
                            }

                            Slurp.plugin.getServer().getScheduler().runTaskLaterAsynchronously(Slurp.plugin, () -> {
                                final ArrayList<SlurpPlayer> players = new ArrayList<>();

                                if (target.equals("<all>")) {
                                    players.addAll(session.getPlayers());
                                } else if (target.equals("<random>")) {
                                    players.addAll(session.getRandomPlayersInSession(1));
                                } else {
                                    players.add(
                                            SlurpPlayerManager.getPlayer(Slurp.plugin.getServer().getPlayer(target)));
                                }

                                var futures = new ArrayList<CompletableFuture<ArrayList<SlurpEntry>>>();

                                for (SlurpPlayer player : players) {
                                    var future = SlurpEntry.create(
                                            new SlurpEntryBuilder(amount, player.getId(), player.getSession().getId(),
                                                    giveable, false));

                                    futures.add(future);
                                }

                                var all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

                                all.thenAccept(v -> {
                                    String targetName = players.size() == 1 ? players.get(0).getUsername()
                                            : players.stream().map(SlurpPlayer::getUsername)
                                                    .reduce((a, b) -> a + ", " + b)
                                                    .orElse("Unknown");

                                    Component suffix = Component.text("[" + amount + "]")
                                            .color(giveable ? NamedTextColor.BLUE : NamedTextColor.RED);

                                    Slurp.plugin.getServer()
                                            .broadcast(TextBuilder
                                                    .prefix(Component.text(message)
                                                            .color(type.equals("penalty") ? NamedTextColor.RED
                                                                    : NamedTextColor.GREEN))
                                                    .append(Component.text(" ")).append(suffix)
                                                    .append(Component.text(" (Player: " + targetName + ")",
                                                            NamedTextColor.GRAY)));

                                    if (rarity > SlurpConfig.getValue(ConfigValue.SOUND_RARITY)) {
                                        for (SlurpPlayer player : players) {
                                            player.getPlayer().playSound(player.getPlayer().getLocation(),
                                                    Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                                        }
                                    }
                                });
                            }, inTime * processedActions * 20);

                            processedActions++;
                        }

                        Slurp.logger.info("Actions processed successfully.");

                        ActionGenerationManager.getHandler(session).clearEventLogs();

                    })
                    .exceptionally(e -> {
                        Slurp.logger.warning("Failed to send request: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    });

        } catch (Exception e) {
            Slurp.logger.warning("Failed to process event logs: " + e.getMessage());
        }

    }

    private final static class GoogleAIRequest {
        private static final String systemPrompt = "You are a helpful assistant that generates actions for a drinking game based on events on a Minecraft server. "
                +
                "Your task is to analyze the provided events and generate a list of actions that contain the penalties and rewards for the players involved. "
                +
                "A penalty would be drinking a certain amount of sips (giveable: false), while a reward would be giving a certain amount of sips to another player (giveable: true). The amount should be a positive integer, and the actions should be fun and engaging for the players. Another reward option is to give some sips to all other players (giveable: false, target: '<all>'). Or a random player (giveable: false, target: '<random>'). "
                +
                "The 'target' field should contain the name of the player who is affected by the action. " +
                "The 'amount' field should contain the number of sips to be given or taken. " +
                "The 'giveable' field should be true if the action is a reward (giving sips to another player) and false if it is a penalty (taking sips from a player). "
                +
                "The 'message' field should be a fun message for the players with the reason for the action. Be creative and make it fun! Messages should be engaging and entertaining, be funny, and relevant to the event that triggered the action. Do not explain the action in the message, just make it fun and engaging. You are allowed to use make fun of the players, make jokes, make references to the game, or use any other creative way to make the message fun. Swear words are allowed, if they make sense in the context of the action. Drop some memes or references to the game, but do not overdo it. "
                + "The 'type' field should be either 'penalty' or 'reward', depending on the action. " +
                "The 'rarity' field should be a number between 1 and 100, indicating the difficulty and rarity of the action. Mining common block or doing trivial actions should not be rare (closer to 1), while doing something extraordinary or very difficult should be rare (closer to 100). "
                + "You should generate a list of actions based on the provided events. " +
                "If there are no actions to be generated, return an empty array. " +
                "Make sure to follow the structure exactly as described, and do not include any additional text or explanations. "
                +
                "The response should be a valid JSON object that matches the structure provided. ";

        private final String prompt;

        public GoogleAIRequest(String prompt) {
            this.prompt = prompt;
        }

        public JsonObject toJson() {
            JsonObject root = new JsonObject();
            JsonObject systemInstructions = new JsonObject();
            JsonArray systemInstructionsParts = new JsonArray();
            JsonObject contents = new JsonObject();
            JsonArray contentsParts = new JsonArray();

            for (String part : systemPrompt.split("\n")) {
                if (part.trim().isEmpty()) {
                    continue;
                }

                JsonObject prompt = new JsonObject();
                prompt.addProperty("text", part);
                systemInstructionsParts.add(prompt);
            }

            systemInstructions.add("parts", systemInstructionsParts);
            root.add("system_instruction", systemInstructions);

            for (String part : prompt.split("\n")) {
                if (part.trim().isEmpty()) {
                    continue;
                }

                JsonObject prompt = new JsonObject();
                prompt.addProperty("text", part);
                contentsParts.add(prompt);
            }

            contents.add("parts", contentsParts);
            root.add("contents", contents);

            // Add responseSchema matching the required structure as inline JSON
            JsonObject generationConfig = JsonParser.parseString("""
                    {
                            "thinkingConfig":{
                                "thinkingBudget":8000
                            },
                            "responseMimeType": "application/json",
                            "responseSchema":{
                                "type":"object",
                                "properties":{
                                    "actions":{
                                    "type":"array",
                                    "items":{
                                        "type":"object",
                                        "properties":{
                                            "target":{
                                                "type":"string"
                                            },
                                            "amount":{
                                                "type":"number"
                                            },
                                            "giveable":{
                                                "type":"boolean"
                                            },
                                            "message":{
                                                "type":"string"
                                            },
                                            "type":{
                                                "type":"string",
                                                "enum":["penalty", "reward"]
                                            },
                                            "rarity":{
                                                "type":"number"
                                            }
                                        }
                                    }
                                    }
                                }
                            }
                        }""").getAsJsonObject();

            root.add("generationConfig", generationConfig);

            JsonArray safetySettings = JsonParser.parseString("""
                    [
                        {
                            "category": "HARM_CATEGORY_HARASSMENT",
                            "threshold": "BLOCK_NONE"
                        },
                        {
                            "category": "HARM_CATEGORY_HATE_SPEECH",
                            "threshold": "BLOCK_NONE"
                        },
                        {
                            "category": "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                            "threshold": "BLOCK_NONE"
                        },
                        {
                            "category": "HARM_CATEGORY_DANGEROUS_CONTENT",
                            "threshold": "BLOCK_NONE"
                        }
                    ]""").getAsJsonArray();

            root.add("safetySettings", safetySettings);

            return root;
        }
    }

}
