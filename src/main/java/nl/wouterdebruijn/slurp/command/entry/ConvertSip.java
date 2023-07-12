package nl.wouterdebruijn.slurp.command.entry;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.ConfigValue;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConvertSip implements TabExecutor {
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

        if (amount < SlurpConfig.getValue(ConfigValue.SIP_SHOT_RATIO)) {
            player.sendMessage(TextBuilder.error("You need to convert at least " + SlurpConfig.getValue(ConfigValue.SIP_SHOT_RATIO) + " sips!"));
            return true;
        }

        if (amount % SlurpConfig.getValue(ConfigValue.SIP_SHOT_RATIO) > 0) {
            player.sendMessage(TextBuilder.error("You need to convert a multiple of " + SlurpConfig.getValue(ConfigValue.SIP_SHOT_RATIO) + " sips!"));
            return true;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Slurp.plugin, () -> {
            // Remove x amount of sips from player
            SlurpEntryBuilder entry = new SlurpEntryBuilder(-amount, 0, slurpPlayer.getUuid(), slurpPlayer.getSession().getUuid(), false, true);
            var task1 = SlurpEntry.create(entry, slurpPlayer.getSession().getToken());

            int sips = amount / SlurpConfig.getValue(ConfigValue.SIP_SHOT_RATIO);

            // Give x amount of shots to player
            SlurpEntryBuilder entry2 = new SlurpEntryBuilder(0, sips, slurpPlayer.getUuid(), slurpPlayer.getSession().getUuid(), false, true);
            var task2 = SlurpEntry.create(entry2, slurpPlayer.getSession().getToken());

            CompletableFuture.allOf(task1, task2).join();
        });

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

        int balance = slurpPlayer.getRemaining().getSips();

        if (balance < SlurpConfig.getValue(ConfigValue.SIP_SHOT_RATIO)) {
            return List.of(new String[]{
                    "0"
            });
        }

        if (args.length == 1) {
            // return list of amounts
            List<String> amounts = new ArrayList<>();
            for (int i = SlurpConfig.getValue(ConfigValue.SIP_SHOT_RATIO); i <= balance; i += SlurpConfig.getValue(ConfigValue.SIP_SHOT_RATIO)) {
                amounts.add(String.valueOf(i));
            }
            return amounts;
        }

        return null;
    }
}
