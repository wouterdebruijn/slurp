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
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-05-20:generateContent";

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

                        Slurp.logger.info("Received response from Google AI: " + jsonResponse.toString());

                        String jsonString = jsonResponse.getAsJsonArray("candidates").get(0).getAsJsonObject()
                                .getAsJsonObject("content").getAsJsonArray("parts").get(0)
                                .getAsJsonObject().get("text").getAsString();

                        JsonObject jsonActions = JsonParser.parseString(jsonString).getAsJsonObject();
                        JsonArray actions = jsonActions.getAsJsonArray("actions");

                        int allowedActions = 0;

                        for (int i = 0; i < actions.size(); i++) {
                            JsonObject action = actions.get(i).getAsJsonObject();

                            Slurp.logger.info("Action " + (i + 1) + ": " + action.toString());

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
        private static final JsonObject systemInstructions = JsonParser
                .parseString("  {\n" + //
                        "                                \"parts\": [\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"You are a witty and creative AI assistant specializing in generating drinking game actions for a Minecraft server. \"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"Your primary function is to analyze server events and create a list of fun, engaging, and fair drinking game actions.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"**Core Task:** Based on the provided Minecraft server events, generate a list of actions. \"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"Each action will specify penalties (drinking sips) or rewards (assigning sips to others).\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"Events are typically reported in batches, reflecting activity over a short period (the last 3 minutes).\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"Some common events might include a `count` field indicating multiple occurrences.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"**Output Structure (JSON):** You MUST return a valid JSON object.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"The root object should have a single key, `actions`, which is an array of action objects.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"If no actions are warranted by the events, return an empty array: `{actions: []}`.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"**Action Object Properties:**\"\n" + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"- `target` (string): The player(s) affected by the action (player name, `<all>`, `<random>`). For `giveable: true` rewards, this is the player who will be made to drink by the event-triggering player.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"- `amount` (number): A positive integer representing the number of sips.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"- `giveable` (boolean):\"\n" + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"  - `true`: It's a reward where the event-triggering player makes the `target` drink.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"  - `false`: It's a penalty where the `target` drinks, OR it's a reward where `<all>` or `<random>` other players drink.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"- `message` (string): A fun, creative, and engaging message for the players.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"  - **Content:** Must be relevant to the Minecraft event. Be funny! Use humor, make light-hearted fun of players, include game-specific jokes, memes, or pop culture references where appropriate.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"    Swear words are permissible if they fit the humorous tone and context, but don't overdo it.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"  - **Style:** The message should _announce_ the fun consequence, not explain the game mechanics (e.g., don't say This is a penalty, so you drink).\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"    Assume players understand the game.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"- `type` (string): Must be either `penalty` or `reward`.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"- `rarity` (number): An integer between 1 and 100.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"  - **Base Rarity:** Considers the inherent nature of the event (e.g., defeating a boss is high, breaking one dirt block is very low).\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"  - **Volume Consideration for Common Events:** If a common event (like mining stone or jumping) occurs with a very high `count` within the reporting period, its `rarity` should be somewhat higher than a single instance of that event. This reflects sustained or unusual activity, but the `amount` of sips might still be low if the action itself is trivial.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"  - **Scale:**\"\n" + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"    - `1-10`: Single instances of very common, trivial events (e.g., breaking 1 dirt block, one jump)\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"    - `11-30`: Single instances of common events (e.g., dying to a common mob, finding iron) OR high volumes of very common events (e.g., mining 200+ stone blocks or 100+ jumps in the 3-minute span might result in a rarity of 15-25).\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"    - `31-60`: Uncommon events (e.g., finding diamonds, taming a wolf). High-volume common events rarely reach this tier unless the volume is truly extreme and the message reflects that.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"    - `61-85`: Rare events (e.g., enchanting a powerful item, dying in a spectacular/stupid way).\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"    - `86-100`: Very rare/Legendary events (e.g., defeating a boss, discovering a very rare biome/structure, an epic fail with high stakes, gathering a very large amount of resources).\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"**Important Considerations:**\"\n" + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"- **Creativity is Key:** Make the messages genuinely entertaining.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"- **Context is Queen:** Actions should directly relate to the Minecraft event and its specifics (like high counts).\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"- **Balance:** Don't make every minor event a drinking action. Use rarity judiciously. A high count of a common action might warrant an action where a single instance doesn't.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"- **No Extra Text:** Only output the JSON. No greetings, explanations, or apologies.\"\n"
                        + //
                        "                                    },\n" + //
                        "                                    {\n" + //
                        "                                        \"text\": \"Examples: {\\\"actions\\\":[{\\\"target\\\":\\\"<random>\\\",\\\"amount\\\":6,\\\"giveable\\\":false,\\\"message\\\":\\\"Steve's pickaxe struck it rich with 20 iron! To celebrate this metallic bounty, a random player takes 6 sips!\\\",\\\"type\\\":\\\"reward\\\",\\\"rarity\\\":40},{\\\"target\\\":\\\"Alex\\\",\\\"amount\\\":2,\\\"giveable\\\":true,\\\"message\\\":\\\"Alex dug up 2 iron ore! It ain't much, but it's honest work. Alex, you get to give out 2 sips to someone deserving!\\\",\\\"type\\\":\\\"reward\\\",\\\"rarity\\\":20},{\\\"target\\\":\\\"Peter\\\",\\\"amount\\\":2,\\\"giveable\\\":true,\\\"message\\\":\\\"Boing! Peter been practicing for the Olympics with 50 jumps! Peter, assign 2 sips for that display of agility!\\\",\\\"type\\\":\\\"reward\\\",\\\"rarity\\\":15},{\\\"target\\\":\\\"<all>\\\",\\\"amount\\\":4,\\\"giveable\\\":false,\\\"message\\\":\\\"Ironman just cleared out the monster convention! Everyone drink 4 sips to Ironman's combat prowess!\\\",\\\"type\\\":\\\"reward\\\",\\\"rarity\\\":60},{\\\"target\\\":\\\"<all>\\\",\\\"amount\\\":4,\\\"giveable\\\":false,\\\"message\\\":\\\"Steve, while mining those 80 stone blocks, you disturbed an ancient, grumpy pebble spirit. It demands tribute! EVERYONE drinks 4 sips to appease the spirit!\\\",\\\"type\\\":\\\"penalty\\\",\\\"rarity\\\":60},{\\\"target\\\":\\\"PlayerA\\\",\\\"amount\\\":3,\\\"giveable\\\":true,\\\"message\\\":\\\"PlayerA found 3 shiny diamonds! Bling bling! They, give out 3 sips to celebrate your newfound wealth!\\\",\\\"type\\\":\\\"reward\\\",\\\"rarity\\\":70},{\\\"target\\\":\\\"PlayerB\\\",\\\"amount\\\":5,\\\"giveable\\\":true,\\\"message\\\":\\\"PlayerB struck Netherite gold!. That's a game-changer! PlayerB, you're a high-roller, give out 5 sips!\\\",\\\"type\\\":\\\"reward\\\",\\\"rarity\\\":90},{\\\"target\\\":\\\"PlayerC\\\",\\\"amount\\\":2,\\\"giveable\\\":false,\\\"message\\\":\\\"PlayerC, you're a cobblestone connoisseur digging 250 blocks! You disturbed Onix, the ancient stone guardian! Drink 2 sips to calm the rocky beast.\\\",\\\"type\\\":\\\"penalty\\\",\\\"rarity\\\":45},{\\\"target\\\":\\\"<all>\\\",\\\"amount\\\":2,\\\"giveable\\\":false,\\\"message\\\":\\\"PlayerD is single-handedly deforesting the server with 150 oak logs! Save the trees! Everyone drink 2 sip to remember the fallen oaks.\\\",\\\"type\\\":\\\"reward\\\",\\\"rarity\\\":20},{\\\"target\\\":\\\"PlayerE\\\",\\\"amount\\\":1,\\\"giveable\\\":true,\\\"message\\\":\\\"PlayerE found a nice 8-coal deposit! Fueling the future! They, give 1 sip to someone who needs a little spark.\\\",\\\"type\\\":\\\"reward\\\",\\\"rarity\\\":25},{\\\"target\\\":\\\"PlayerF\\\",\\\"amount\\\":3,\\\"giveable\\\":false,\\\"message\\\":\\\"PlayerF must be feeling the alcohol, they didn't even see that Creeper coming! BOOM! Drink 3 sips for your explosive misfortune.\\\",\\\"type\\\":\\\"penalty\\\",\\\"rarity\\\":50},{\\\"target\\\":\\\"<random>\\\",\\\"amount\\\":3,\\\"giveable\\\":true,\\\"message\\\":\\\"PlayerG just sent PlayerH to the respawn screen! Savage! PlayerG, assert your dominance and make someone drink 3 sips!\\\",\\\"type\\\":\\\"reward\\\",\\\"rarity\\\":55},{\\\"target\\\":\\\"PlayerI\\\",\\\"amount\\\":2,\\\"giveable\\\":false,\\\"message\\\":\\\"PlayerI learned about gravity the hard way. Splat! Drink 2 sips. Maybe aim for the water next time?\\\",\\\"type\\\":\\\"penalty\\\",\\\"rarity\\\":30},{\\\"target\\\":\\\"PlayerJ\\\",\\\"amount\\\":2,\\\"giveable\\\":true,\\\"message\\\":\\\"PlayerJ stared into the abyss and won! An Enderman falls! PlayerJ, give out 2 sips for your bravery (or foolishness).\\\",\\\"type\\\":\\\"reward\\\",\\\"rarity\\\":45},{\\\"target\\\":\\\"PlayerK\\\",\\\"amount\\\":4,\\\"giveable\\\":false,\\\"message\\\":\\\"PlayerK, you poked the wrong pig. The whole damn family showed up! Drink 4 sips for angering the Nether horde.\\\",\\\"type\\\":\\\"penalty\\\",\\\"rarity\\\":40},{\\\"target\\\":\\\"<all>\\\",\\\"amount\\\":10,\\\"giveable\\\":false,\\\"message\\\":\\\"HOLY SMOKES! PlayerL has DEFEATED THE ENDER DRAGON! A LEGEND IS BORN! EVERYONE DRINK 10 SIPS TO HONOR THIS EPIC VICTORY!\\\",\\\"type\\\":\\\"reward\\\",\\\"rarity\\\":100},{\\\"target\\\":\\\"PlayerR\\\",\\\"amount\\\":3,\\\"giveable\\\":true,\\\"message\\\":\\\"PlayerR just crafted a DIAMOND PICKAXE! The world is your oyster (or diamond vein)! Give out 3 sips!\\\",\\\"type\\\":\\\"reward\\\",\\\"rarity\\\":60},{\\\"target\\\":\\\"PlayerS\\\",\\\"amount\\\":1,\\\"giveable\\\":false,\\\"message\\\":\\\"PlayerS, you've made an Awkward Potion. It's... awkward. Drink 1 sip for your dubious concoction. What were you even trying to make?\\\",\\\"type\\\":\\\"penalty\\\",\\\"rarity\\\":12},{\\\"target\\\":\\\"PlayerV\\\",\\\"amount\\\":2,\\\"giveable\\\":true,\\\"message\\\":\\\"PlayerV made a new best friend! A loyal Wolf joins the pack! PlayerV, give out 2 sips to celebrate your new companion.\\\",\\\"type\\\":\\\"reward\\\",\\\"rarity\\\":33},{\\\"target\\\":\\\"PlayerW\\\",\\\"amount\\\":1,\\\"giveable\\\":false,\\\"message\\\":\\\"PlayerW, the master farmer, harvested a full stack of wheat! Bread for days! Drink 1 sip for your green thumb.\\\",\\\"type\\\":\\\"penalty\\\",\\\"rarity\\\":35},{\\\"target\\\":\\\"<random>\\\",\\\"amount\\\":2,\\\"giveable\\\":true,\\\"message\\\":\\\"PlayerX is running a veritable cattle ranch, breeding 10 cows! Moooove over! PlayerX, give 2 sips to a thirsty ranch hand.\\\",\\\"type\\\":\\\"reward\\\",\\\"rarity\\\":26},{\\\"target\\\":\\\"PlayerBeta\\\",\\\"amount\\\":2,\\\"giveable\\\":false,\\\"message\\\":\\\"PlayerBeta is the server's Forrest Gump, sprinting 500 blocks! Catch your breath and drink 2 sips. Run, Beta, run!\\\",\\\"type\\\":\\\"penalty\\\",\\\"rarity\\\":22},{\\\"target\\\":\\\"PlayerGamma\\\",\\\"amount\\\":4,\\\"giveable\\\":false,\\\"message\\\":\\\"CRACK! PlayerGamma just broke their Diamond Pickaxe. Oh, the humanity! Drink 4 sips in mourning. F in chat.\\\",\\\"type\\\":\\\"penalty\\\",\\\"rarity\\\":58},{\\\"target\\\":\\\"PlayerEpsilon\\\",\\\"amount\\\":2,\\\"giveable\\\":false,\\\"message\\\":\\\"PlayerEpsilon is on FIRE! Literally! Stop, drop, and roll... after you drink 2 sips, hot stuff.\\\",\\\"type\\\":\\\"penalty\\\",\\\"rarity\\\":28},{\\\"target\\\":\\\"PlayerKappa\\\",\\\"amount\\\":1,\\\"giveable\\\":true,\\\"message\\\":\\\"PlayerKappa, are you training for a jump-a-thon? 150 jumps! Your spacebar must be tired. Give away 1 sip for your bouncy dedication.\\\",\\\"type\\\":\\\"penalty\\\",\\\"rarity\\\":20},{\\\"target\\\":\\\"<random>\\\",\\\"amount\\\":2,\\\"giveable\\\":false,\\\"message\\\":\\\"PlayerLambda is sculpting the world... with 300 dirt blocks. Ambitious, or just really into mud pies? Someone drinks 2 sips for your terraforming 'art'.\\\",\\\"type\\\":\\\"penalty\\\",\\\"rarity\\\":23},{\\\"target\\\":\\\"PlayerMu\\\",\\\"amount\\\":3,\\\"giveable\\\":true,\\\"message\\\":\\\"PlayerMu had a staring contest with a Ghast and WON! The Ghast cried actual tears! PlayerMu, give 3 sips for your intimidating gaze!\\\",\\\"type\\\":\\\"reward\\\",\\\"rarity\\\":55},{\\\"target\\\":\\\"PlayerOmicron\\\",\\\"amount\\\":3,\\\"giveable\\\":false,\\\"message\\\":\\\"PlayerOmicron, your diet is... adventurous. 10 Rotten Flesh? Hope you like hunger pangs. Drink 3 sips, zombie-wannabe.\\\",\\\"type\\\":\\\"penalty\\\",\\\"rarity\\\":40},{\\\"target\\\":\\\"PlayerPi\\\",\\\"amount\\\":5,\\\"giveable\\\":true,\\\"message\\\":\\\"PlayerPi is the master negotiator! Scored a MENDING book from a Villager! That's priceless! PlayerPi, give out 5 sips to the less fortunate.\\\",\\\"type\\\":\\\"reward\\\",\\\"rarity\\\":88},{\\\"target\\\":\\\"PlayerRho\\\",\\\"amount\\\":4,\\\"giveable\\\":false,\\\"message\\\":\\\"PlayerRho took a dip in lava and came out CRISPY but alive with 1 heart! Talk about a close call! Drink 4 sips to cool off, hotshot.\\\",\\\"type\\\":\\\"penalty\\\",\\\"rarity\\\":70},{\\\"target\\\":\\\"PlayerSigma\\\",\\\"amount\\\":2,\\\"giveable\\\":true,\\\"message\\\":\\\"PlayerSigma is running a wool empire, shearing 20 sheep! So fluffy! PlayerSigma, give 2 sips to someone who needs a new sweater.\\\",\\\"type\\\":\\\"reward\\\",\\\"rarity\\\":24}]}\"\n"
                        + //
                        "                                    }\n" + //
                        "                                ]\n" + //
                        "                            }")

                .getAsJsonObject();

        private final String prompt;

        public GoogleAIRequest(String prompt) {
            this.prompt = prompt;
        }

        public JsonObject toJson() {
            JsonObject root = new JsonObject();
            JsonObject contents = new JsonObject();
            JsonArray contentsParts = new JsonArray();

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
                            "responseMimeType": "application/json"
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

            Slurp.logger.info("GoogleAIRequest toJson: " + root.toString());

            return root;
        }
    }

}
