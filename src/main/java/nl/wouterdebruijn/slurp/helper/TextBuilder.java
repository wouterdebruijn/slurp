package nl.wouterdebruijn.slurp.helper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class TextBuilder {
    private static Component prefix() {
        return Component.text(SlurpConfig.prefix());
    }
    public static Component error(String message) {
        return prefix().append(Component.text(message).color(NamedTextColor.RED));
    }
    public static Component error(Component message) {
        return prefix().append(message.color(NamedTextColor.RED));
    }
    public static Component info(String message) {
        return prefix().append(Component.text(message).color(NamedTextColor.GRAY));
    }
    public static Component info(Component message) {
        return prefix().append(message.color(NamedTextColor.GRAY));
    }
    public static Component success(String message) {
        return prefix().append(Component.text(message).color(NamedTextColor.GREEN));
    }
    public static Component success(Component message) {
        return prefix().append(message.color(NamedTextColor.GREEN));
    }
    public static Component warning(String message) {
        return prefix().append(Component.text(message).color(NamedTextColor.YELLOW));
    }
    public static Component warning(Component message) {
        return prefix().append(message.color(NamedTextColor.YELLOW));
    }
}
