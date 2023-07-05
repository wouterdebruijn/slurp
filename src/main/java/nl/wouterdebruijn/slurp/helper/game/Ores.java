package nl.wouterdebruijn.slurp.helper.game;

import org.bukkit.Material;

public enum Ores {

    COAL(Material.COAL_ORE, "ores.coal.sip", "ores.coal.chance", 1000),
    DEEP_COAL(Material.DEEPSLATE_COAL_ORE, "ores.deepcoal.sip", "ores.deepcoal.chance", 1000),
    COPPER(Material.COPPER_ORE, "ores.copper.sip", "ores.copper.chance", 600),
    DEEP_COPPER(Material.DEEPSLATE_COPPER_ORE, "ores.deepcopper.sip", "ores.deepcopper.chance", 600),
    LAPIS(Material.LAPIS_ORE, "ores.lapis.sip", "ores.lapis.chance", 100),
    DEEP_LAPIS(Material.DEEPSLATE_LAPIS_ORE, "ores.deeplapis.sip", "ores.deeplapis.chance", 100),
    IRON(Material.IRON_ORE, "ores.iron.sip", "ores.iron.chance", 500),
    DEEP_IRON(Material.DEEPSLATE_IRON_ORE, "ores.deepiron.sip", "ores.deepiron.chance", 500),
    GOLD(Material.GOLD_ORE, "ores.gold.sip", "ores.gold.chance", 150),
    DEEP_GOLD(Material.DEEPSLATE_GOLD_ORE, "ores.deepgold.sip", "ores.deepgold.chance", 150),
    REDSTONE(Material.REDSTONE_ORE, "ores.redstone.sip", "ores.redstone.chance", 100),
    DEEP_REDSTONE(Material.DEEPSLATE_REDSTONE_ORE, "ores.deepredstone.sip", "ores.deepredstone.chance", 100),
    DIAMOND(Material.DIAMOND_ORE, "ores.diamond.sip", "ores.diamond.chance", 50),
    DEEP_DIAMOND(Material.DEEPSLATE_DIAMOND_ORE, "ores.deepdiamond.sip", "ores.deepdiamond.chance", 50),
    EMERALD(Material.EMERALD_ORE, "ores.emerald.sip", "ores.emerald.chance", 80),
    DEEP_EMERALD(Material.DEEPSLATE_EMERALD_ORE, "ores.deepemerald.sip", "ores.deepemerald.chance", 80);

    private final Material type;
    private final String path;
    private final String chancePath;
    private final int defaultChanceValue;

    Ores(Material type, String path, String chancePath, int defaultChanceValue) {
        this.type = type;
        this.path = path;
        this.chancePath = chancePath;
        this.defaultChanceValue = defaultChanceValue;
    }

    public Material getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public String getChancePath() {
        return chancePath;
    }

    public int getDefaultChanceValue() {
        return defaultChanceValue;
    }
}
