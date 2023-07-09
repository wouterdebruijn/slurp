package nl.wouterdebruijn.slurp.helper;

public enum ConfigValue {
    DIFFICULTY_MULTIPLIER("game.difficulty-multiplier"),
    SIP_SHOT_RATIO("game.sip-shot-ratio");

    private final String confPath;

    ConfigValue(String confPath) {
        this.confPath = confPath;
    }

    public String getPath() {
        return confPath;
    }
}
