package nl.wouterdebruijn.slurp.controller;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class MessageController {
    public static void sendMessage(Player player, String... string) {
        player.sendMessage(string);
    }

    public static void sendMessage(Player player, boolean prefix, String... string) {
        if (prefix) {
            ArrayList<String> stringArrayList = new ArrayList<>();
            stringArrayList.add(ConfigController.getString("slurp-prefix"));
            stringArrayList.addAll(Arrays.asList(string));
            player.sendMessage(String.join("", stringArrayList));
        }
    }
}
