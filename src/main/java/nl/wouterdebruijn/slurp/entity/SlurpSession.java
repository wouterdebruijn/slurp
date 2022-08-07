package nl.wouterdebruijn.slurp.entity;

import com.google.gson.annotations.SerializedName;

public class SlurpSession {
    public String uuid;
    @SerializedName("short")
    public String sessionShort;

    public boolean active;
}
