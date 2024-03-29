package nl.wouterdebruijn.slurp.command.entry;

import nl.wouterdebruijn.slurp.Slurp;
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

/**
 * Lets the player take a shot. Updating the Slurp API
 * Players are allowed to take more shots than they have, possibly resulting in a negative remaining count
 */
public class TakeShot implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (slurpPlayer == null) {
            player.sendMessage(TextBuilder.error("You are not in a session!"));
            return true;
        }

        if (args.length != 1) {
            return false;
        }

        int amount = Integer.parseInt(args[0]);

        if (amount < 1) {
            player.sendMessage(TextBuilder.error("You can't take less than 1 shot!"));
            return true;
        }

        int sips = 0;
        if (amount > slurpPlayer.getRemaining().getShots()) {
            int remainingAmount = amount - slurpPlayer.getRemaining().getShots();
            int originalAmount = amount;
            amount = slurpPlayer.getRemaining().getShots();

            // Convert the remaining amount to sips
            sips = remainingAmount * SlurpConfig.getValue(ConfigValue.SIP_SHOT_RATIO);

            // Don't convert if the player doesn't have enough sips. Just take the shots (goes negative)
            if (sips > slurpPlayer.getRemaining().getSips()) {
                amount = originalAmount;
                sips = 0;
            }

            player.sendMessage(TextBuilder.warning(String.format("You don't have enough shots! You took %d %s and %d %s instead!", amount, ConsumableGivingHandler.getTextShots(amount), sips, ConsumableGivingHandler.getTextSips(sips))));
        } else {
            player.sendMessage(TextBuilder.success(String.format("You took %d %s!", amount, ConsumableGivingHandler.getTextShots(amount))));
        }

        int finalSips = sips;
        int finalAmount = amount;
        Bukkit.getScheduler().runTaskAsynchronously(Slurp.plugin, () -> {
            // Give the target player the shots
            SlurpEntryBuilder updateEntry = new SlurpEntryBuilder(-finalSips, -finalAmount, slurpPlayer.getUuid(), slurpPlayer.getSession().getUuid(), false, false);
            Bukkit.getScheduler().runTaskAsynchronously(Slurp.plugin, () -> SlurpEntry.createDirect(updateEntry, slurpPlayer.getSession().getToken()).join());
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

        int balance = slurpPlayer.getRemaining().getShots();

        if (args.length == 1) {
            // Return list of amounts
            List<String> amounts = new ArrayList<>();
            for (int i = 1; i <= balance; i++) {
                amounts.add(String.valueOf(i));
            }
            return amounts;
        }

        return null;
    }
}
