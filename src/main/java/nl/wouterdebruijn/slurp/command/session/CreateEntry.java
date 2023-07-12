package nl.wouterdebruijn.slurp.command.session;

import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CreateEntry implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        // <player> <type> <amount> <giveable> <transfer>

        if (args.length != 5) {
            player.sendMessage(TextBuilder.error("Invalid arguments"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(TextBuilder.error("Player not found"));
            return true;
        }

        boolean isShot = args[1].equalsIgnoreCase("SHOT");

        int amount = Integer.parseInt(args[2]);

        boolean giveable = args[3].equalsIgnoreCase("TRUE");
        boolean transfer = args[4].equalsIgnoreCase("TRUE");

        int shotAmount = isShot ? amount : 0;
        int sipAmount = !isShot ? amount : 0;

        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);
        SlurpEntryBuilder entry = new SlurpEntryBuilder(shotAmount, sipAmount, slurpPlayer.getUuid(), slurpPlayer.getSession().getUuid(), giveable, transfer);

        SlurpEntry.create(entry, slurpPlayer.getSession().getToken());

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TextBuilder.error("You must be a player to execute this command!"));
            return null;
        }

        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(player, slurpPlayer)) {
            return null;
        }

        // First argument is a player
        if (args.length == 1) {
            ArrayList<String> playerList = new ArrayList<>();

            for (Player playerIterator : Bukkit.getOnlinePlayers()) {
                // Check if player is in a session.
                SlurpPlayer slurpPlayerIterator = SlurpPlayerManager.getPlayer(playerIterator);
                if (SlurpPlayerManager.checkNullSilent(playerIterator, slurpPlayerIterator)) {
                    continue;
                }

                playerList.add(player.getName());
            }
        }

        if (args.length == 2) {
            return new ArrayList<>(Arrays.asList("SIP", "SHOT"));
        }

        if (args.length == 3) {
            ArrayList<String> list = new ArrayList<>();

            for (int i=0; i<999; i++) {
                list.add(String.valueOf(i));
            }

            return list;
        }

        if (args.length == 4 || args.length == 5) {
            return new ArrayList<>(Arrays.asList("TRUE", "FALSE"));
        }

        return null;
    }
}
