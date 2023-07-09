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
 * Lets the player take a sip. Updating the Slurp API
 * Players are allowed to take more sips than they have, possibly resulting in a negative remaining count
 */
public class TakeSip implements TabExecutor {
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
            player.sendMessage(TextBuilder.error("You can't take less than 1 sip!"));
            return true;
        }

        int shots = 0;
        if (amount > slurpPlayer.getRemaining().getSips()) {
            int remainingAmount = amount - slurpPlayer.getRemaining().getSips();
            int originalAmount = amount;
            amount = slurpPlayer.getRemaining().getSips();

            // Convert the remaining amount to sips
            int spare = remainingAmount % SlurpConfig.getValue(ConfigValue.SIP_SHOT_RATIO);
            shots = (remainingAmount - spare) / SlurpConfig.getValue(ConfigValue.SIP_SHOT_RATIO);

            if (shots < 1) {
                // Don't convert to shots if the player doesn't have enough sips, just take the sips (go negative)
                amount = originalAmount;
                shots = 0;
            }

            if (shots > slurpPlayer.getRemaining().getShots()) {
                shots = slurpPlayer.getRemaining().getShots();
            }

            player.sendMessage(TextBuilder.warning(String.format("You don't have enough shots! You took %d %s and %d %s instead!", amount, ConsumableGivingHandler.getTextSips(amount), shots, ConsumableGivingHandler.getTextShots(shots))));
        } else {
            player.sendMessage(TextBuilder.success(String.format("You took %d %s!", amount, ConsumableGivingHandler.getTextShots(amount))));
        }

        // Give the target player the shots
        SlurpEntryBuilder updateEntry = new SlurpEntryBuilder(-amount, -shots, slurpPlayer.getUuid(), slurpPlayer.getSession().getUuid(), false, false);
        Bukkit.getScheduler().runTaskAsynchronously(Slurp.plugin, () -> SlurpEntry.create(updateEntry, slurpPlayer.getSession().getToken()).join());

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);
        int balance = slurpPlayer.getRemaining().getSips();

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
