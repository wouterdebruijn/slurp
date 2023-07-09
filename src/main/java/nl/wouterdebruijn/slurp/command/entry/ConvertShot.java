package nl.wouterdebruijn.slurp.command.entry;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.exceptions.SlurpMessageException;
import nl.wouterdebruijn.slurp.helper.ConfigValue;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.handlers.ConsumableGivingHandler;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConvertShot implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (args.length != 1) {
            return false;
        }

        if (slurpPlayer == null) {
            player.sendMessage(TextBuilder.error("You are not in a session!"));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(TextBuilder.error("Invalid amount!"));
            return true;
        }

        // Remove x amount of shots from player
        SlurpEntryBuilder entry = new SlurpEntryBuilder(0, -amount, slurpPlayer.getUuid(), slurpPlayer.getSession().getUuid(), false, true);
        var task1 = SlurpEntry.create(entry, slurpPlayer.getSession().getToken());

        int sips = SlurpConfig.getValue(ConfigValue.SIP_SHOT_RATIO) * amount;

        // Give x amount of sips to player
        SlurpEntryBuilder entry2 = new SlurpEntryBuilder(sips, 0, slurpPlayer.getUuid(), slurpPlayer.getSession().getUuid(), false, true);
        var task2 = SlurpEntry.create(entry2, slurpPlayer.getSession().getToken());

        CompletableFuture.allOf(task1, task2).join();
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);
        int balance = slurpPlayer.getRemaining().getShots();

        if (args.length == 1) {
            // return list of amounts
            List<String> amounts = new ArrayList<>();
            for (int i = 1; i <= balance; i++) {
                amounts.add(String.valueOf(i));
            }
            return amounts;
        }

        return null;
    }
}
