package nl.wouterdebruijn.slurp.command.entry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

public class GiveShot implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        Player player = (Player) sender;
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (slurpPlayer == null) {
            player.sendMessage(TextBuilder.error("You are not in a session!"));
            return true;
        }

        if (args.length < 1 || args.length > 2) {
            return false;
        }

        int sipShotRatio = SlurpConfig.getValue(ConfigValue.SIP_SHOT_RATIO);

        String targetName = args[0];
        int amount = sipShotRatio; // Default to 1 shot

        if (args.length == 2) {
            try {
                amount = Integer.parseInt(args[1]) * sipShotRatio; // Convert sips to shots
            } catch (NumberFormatException e) {
                player.sendMessage(TextBuilder.error("Invalid amount!"));
                return true;
            }
        }

        int balance = slurpPlayer.getGiveable();

        if (amount > balance) {
            player.sendMessage(TextBuilder.error("You don't have enough shots to give away!"));
            return true;
        }

        Player target = player.getServer().getPlayer(targetName);

        if (target == null) {
            player.sendMessage(TextBuilder.error("Player not found!"));
            return true;
        }

        SlurpPlayer targetSlurpPlayer = SlurpPlayerManager.getPlayer(target);

        if (targetSlurpPlayer == null) {
            player.sendMessage(TextBuilder.error("Player not found!"));
            return true;
        }

        int finalAmount = amount;

        Bukkit.getScheduler().runTaskAsynchronously(Slurp.plugin, () -> {
            try {
                // Give the target player the sips
                SlurpEntryBuilder entry = new SlurpEntryBuilder(finalAmount, targetSlurpPlayer.getId(),
                        slurpPlayer.getSession().getId(), false, false);
                CompletableFuture<ArrayList<SlurpEntry>> addSips = ConsumableGivingHandler.playerGiveConsumable(target,
                        player, entry);

                // Wait for the sips to be added to the player, if it fails, it will throw an
                // exception
                addSips.get();

                // Remove the sips from the player
                SlurpEntryBuilder giveableUpdateEntry = new SlurpEntryBuilder(-finalAmount, slurpPlayer.getId(),
                        slurpPlayer.getSession().getId(), true, false);
                SlurpEntry.createDirect(giveableUpdateEntry).get();
            } catch (Exception e) {
                Throwable cause = e.getCause();
                if (cause instanceof SlurpMessageException) {
                    player.sendMessage(TextBuilder.error(cause.getMessage()));
                } else {
                    player.sendMessage(TextBuilder.error("Something went wrong!"));
                }
            }
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

        int balance = slurpPlayer.getGiveable();

        if (args.length == 1) {
            // return list of players
            List<String> players = new ArrayList<>();
            for (Player p : player.getServer().getOnlinePlayers()) {
                if (!p.getName().equals(player.getName())) {
                    players.add(p.getName());
                }
            }
            return players;
        }

        if (args.length == 2) {
            // return list of amounts
            List<String> amounts = new ArrayList<>();
            for (int i = 1; i * SlurpConfig.getValue(ConfigValue.SIP_SHOT_RATIO) <= balance; i++) {
                amounts.add(String.valueOf(i));
            }
            return amounts;
        }

        return null;
    }
}
