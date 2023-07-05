package nl.wouterdebruijn.slurp.helper;

import org.jetbrains.annotations.Nullable;

public enum ConfigValue {

    CHANCES_LUCYSTONE("chances.lucyStone"),
    CHANCES_BURNING_HANDS("chances.burnHands"),
    CHANCES_CHOKING("chances.choking"),
    CHANCES_FALL_DAMAGE("chances.fallDamage"),
    CHANCES_SPRINTING("chances.sprintingChance"),
    CHANCES_SWIMMING("chances.swimmingChance"),
    CHANCES_BOAT("chances.boatChance"),
    SIP_LUCYSTONE("drinks.sips.lucyStone"),
    SIP_BURNING_HANDS("drinks.sips.burnHands"),
    SIP_CHOKING("drinks.sips.choking"),
    SIP_FALL_DAMAGE("drinks.sips.fallDamage"),
    SIP_DIE("drinks.sips.die"),
    SIP_KILLER("drinks.sips.killer"),
    SIP_KILLED_ANIMAL("drinks.sips.killAnimal"),
    SIP_SPRINTING("drinks.sips.sprinting"),
    SIP_SWIMMING("drinks.sips.swimming"),
    SIP_BOAT("drinks.sips.boat"),
    SHOT_LUCYSTONE("drinks.shots.lucyStone"),
    SHOT_BURNING_HANDS("drinks.shots.burnHands"),
    SHOT_CHOKING("drinks.shots.choking"),
    SHOT_FALL_DAMAGE("drinks.shots.fallDamage"),
    SHOT_DIE("drinks.shots.die"),
    SHOT_KILLER("drinks.shots.killer"),
    SHOT_KILLED_ANIMAL("drinks.shots.killAnimal"),
    SHOT_SPRINTING("drinks.shots.sprinting"),
    SHOT_SWIMMING("drinks.shots.swimming"),
    SHOT_BOAT("drinks.shots.boat");

    private final String confPath;

    ConfigValue(String confPath) {
        this.confPath = confPath;
    }

    public String getPath() {
        return confPath;
    }
}
