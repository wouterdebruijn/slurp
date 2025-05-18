package nl.wouterdebruijn.slurp.endpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PocketBaseGson {
    // 2025-05-18 17:54:37.171Z
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss.SSS'Z'").create();

    public static Gson getGson() {
        return gson;
    }
}
