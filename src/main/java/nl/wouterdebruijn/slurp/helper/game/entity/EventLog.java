package nl.wouterdebruijn.slurp.helper.game.entity;

import com.google.gson.JsonObject;

public class EventLog {
    private final String eventName;
    private final JsonObject data;
    private int count = 0;

    public EventLog(String eventName, JsonObject data) {
        this.eventName = eventName;
        this.data = data;
        this.count = 1;
    }

    public String getEventName() {
        return eventName;
    }

    public JsonObject getData() {
        return data;
    }

    public String getDataString() {
        return data.toString();
    }

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        this.count++;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("eventName", eventName);
        json.addProperty("count", count);
        json.add("data", data);
        return json;

    }
}
