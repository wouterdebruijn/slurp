package nl.wouterdebruijn.slurp.endpoint;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.kyori.adventure.text.Component;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.game.entity.EventLog;

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

    public static void generateActions(ArrayList<EventLog> eventLogs) {
        JsonArray jsonArray = new JsonArray();

        if (instance == null) {
            Slurp.logger.warning("GoogleAI instance is not initialized.");
            return;
        }

        for (EventLog eventLog : eventLogs) {
            jsonArray.add(eventLog.toJson());
        }

        GoogleAIRequest request = new GoogleAIRequest(jsonArray.toString());

        Slurp.logger.info("Sending request to Google AI: " + request.toJson().toString());

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(
                            API_URL + "?key=" + instance.token))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(request.toJson().toString()))
                    .build();

            eventLogs.clear();

            HttpClient client = HttpClient.newHttpClient();

            client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
                        String jsonString = jsonResponse.getAsJsonArray("candidates").get(0).getAsJsonObject()
                                .getAsJsonObject("content").getAsJsonArray("parts").get(0)
                                .getAsJsonObject().get("text").getAsString();

                        JsonObject jsonActions = JsonParser.parseString(jsonString).getAsJsonObject();
                        JsonArray actions = jsonActions.getAsJsonArray("actions");

                        for (int i = 0; i < actions.size(); i++) {
                            JsonObject action = actions.get(i).getAsJsonObject();
                            String target = action.get("target").getAsString();
                            double amount = action.get("amount").getAsDouble();
                            boolean giveable = action.get("giveable").getAsBoolean();
                            String title = action.get("title").getAsString();
                            String message = action.get("message").getAsString();

                            // Process the action here
                            Slurp.plugin.getServer().broadcast(Component.text(
                                    String.format("Action: %s\nAmount: %.2f\nGiveable: %s\nTitle: %s\nMessage: %s",
                                            target, amount, giveable, title, message)));

                            // TODO: Implement the logic to apply the action to the player

                        }
                        Slurp.aiHandlerEvent.clearEventLogs();

                    })
                    .exceptionally(e -> {
                        Slurp.logger.warning("Failed to send request: " + e.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            Slurp.logger.warning("Failed to process event logs: " + e.getMessage());
        }

    }

    private final static class GoogleAIRequest {
        private static final String systemPrompt = "You are an excellent gamemaster monitoring player events from a Minecraft server. Events are received in bulk as a JSON array, and you process all events at once. Each event has a small chance of triggering a dynamic action that awards or deducts points from players. Points can be negative or transferable, depending on the event. Actions must always reflect the nature of the event: for example, mining diamonds might grant positive, transferable points, while dying to a creeper might deduct non-transferable points. No event should award or deduct more than a handful of points, and the amount should match the event's difficulty. Be very sparing with points for frequent events, as these should not be rewarded often.\n"
                +
                "You will receive events in JSON format. Not every event should result in an action—actions should be spontaneous and rare, especially for common events. Be extremely creative with the title and message; make them fun and non-repetitive. Slightly explicit or edgy contents is allowed here, like swearing or making fun of a player, if it contributes to to the funniness of a joke. You should return multiple actions to take if applicable.\n"
                +
                "Do not provide any other information, comments, or explanations for any steps taken";
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
                                            "title":{
                                                "type":"string"
                                            },
                                            "message":{
                                                "type":"string"
                                            }
                                        }
                                    }
                                    }
                                }
                            }
                        }""").getAsJsonObject();

            root.add("generationConfig", generationConfig);

            return root;
        }
    }

}
