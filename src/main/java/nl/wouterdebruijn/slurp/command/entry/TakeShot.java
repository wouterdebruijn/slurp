package nl.wouterdebruijn.slurp.command.entry;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.ConfigValue;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.handlers.ConsumableGivingHandler;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;

public class TakeShot implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        Player player = (Player) sender;
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (slurpPlayer == null) {
            player.sendMessage(TextBuilder.error("You are not in a session!"));
            return true;
        }

        int sipShotRatio = SlurpConfig.getValue(ConfigValue.SIP_SHOT_RATIO);
        int amount = sipShotRatio; // Default to 1 shot

        if (args.length == 1) {
            // Set the amount based on the argument
            amount = Integer.parseInt(args[0]) * sipShotRatio; // Convert sips to shots
        }

        if (amount < 1) {
            player.sendMessage(TextBuilder.error("You can't take less than 1 shot!"));
            return true;
        }

        int finalAmount = amount;
        Bukkit.getScheduler().runTaskAsynchronously(Slurp.plugin, () -> {
            // Give the target player the shots
            SlurpEntryBuilder updateEntry = new SlurpEntryBuilder(-finalAmount, slurpPlayer.getId(),
                    slurpPlayer.getSession().getId(), false, false);
            SlurpEntry.createDirect(updateEntry).join();

            player.sendMessage(
                    TextBuilder.success("You took " + ConsumableGivingHandler.getUnitText(finalAmount) + "."));
        });
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TextBuilder.error("You must be a player to execute this command!"));
            return null;
        }

        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (SlurpPlayerManager.checkNullSilent(player, slurpPlayer)) {
            return null;
        }

        int balance = slurpPlayer.getRemaining();

        if (args.length == 1) {
            // Return list of amounts
            List<String> amounts = new ArrayList<>();
            for (int i = 1; i * SlurpConfig.getValue(ConfigValue.SIP_SHOT_RATIO) <= balance; i++) {
                amounts.add(String.valueOf(i));
            }
            return amounts;
        }

        return null;
    }
}
