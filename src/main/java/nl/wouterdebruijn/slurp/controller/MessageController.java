package nl.wouterdebruijn.slurp.controller;

import org.bukkit.entity.Player;

public class MessageController {
    public static void sendMessage(Player player, String... string) {
        player.sendMessage(string);
    }
}
