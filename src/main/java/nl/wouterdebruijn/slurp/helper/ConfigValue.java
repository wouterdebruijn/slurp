package nl.wouterdebruijn.slurp.helper;

import org.jetbrains.annotations.Nullable;

public enum ConfigValue {

    CHANCES_LUCYSTONE("chances.lucyStone", 1000000),
    CHANCES_BURNING_HANDS("chances.burnHands", 1000000),
    CHANCES_CHOKING("chances.choking", 1000000),
    CHANCES_FALL_DAMAGE("chances.fallDamage", 1000000),
    CHANCES_SPRINTING("chances.sprintingChance", 1000000),
    CHANCES_SWIMMING("chances.swimmingChance", 1000000),
    CHANCES_BOAT("chances.boatChance", 1000000),
    SIP_LUCYSTONE("drinks.sips.lucyStone", 0),
    SIP_BURNING_HANDS("drinks.sips.burnHands", 0),
    SIP_CHOKING("drinks.sips.choking", 4),
    SIP_FALL_DAMAGE("drinks.sips.fallDamage", 2),
    SIP_DIE("drinks.sips.die", 2),
    SIP_KILLER("drinks.sips.killer", 2),
    SIP_KILLED_ANIMAL("drinks.sips.killAnimal", 3),
    SIP_SPRINTING("drinks.sips.sprinting", 2),
    SIP_SWIMMING("drinks.sips.swimming", 2),
    SIP_BOAT("drinks.sips.boat", 3),
    SHOT_LUCYSTONE("drinks.shots.lucyStone", 1),
    SHOT_BURNING_HANDS("drinks.shots.burnHands", 0),
    SHOT_CHOKING("drinks.shots.choking", 0),
    SHOT_FALL_DAMAGE("drinks.shots.fallDamage", 0),
    SHOT_DIE("drinks.shots.die", 0),
    SHOT_KILLER("drinks.shots.killer", 1),
    SHOT_KILLED_ANIMAL("drinks.shots.killAnimal", 0),
    SHOT_SPRINTING("drinks.shots.sprinting", 0),
    SHOT_SWIMMING("drinks.shots.swimming", 0),
    SHOT_BOAT("drinks.shots.boat", 0);

    private final String confPath;
    private final int value;

    ConfigValue(String confPath, int value) {
        this.confPath = confPath;
        this.value = value;
    }

    public String getPath() {
        return confPath;
    }

    public int getDefaultValue() {
        return value;
    }
}
