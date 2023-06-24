package nl.wouterdebruijn.slurp.command.entry;

import nl.wouterdebruijn.slurp.exceptions.SlurpMessageException;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.handlers.ConsumableGivingHandler;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GiveSip implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (args.length != 2) {
            return false;
        }

        if (slurpPlayer == null) {
            player.sendMessage(TextBuilder.error("You are not in a session!"));
            return true;
        }

        String targetName = args[0];
        int amount = Integer.parseInt(args[1]);
        int balance = slurpPlayer.getGiveable().getSips();

        if (amount > balance) {
            player.sendMessage(TextBuilder.error("You don't have enough sips to give away!"));
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

        try {
            // Give the target player the sips
            SlurpEntryBuilder entry = new SlurpEntryBuilder(amount, 0, targetSlurpPlayer.getUuid(), slurpPlayer.getSession().getUuid(), false, false);
            ConsumableGivingHandler.playerGiveConsumable(target, player, entry).get();

            // Update the balance of the origin player
            SlurpEntryBuilder giveableUpdateEntry = new SlurpEntryBuilder(-amount, 0, slurpPlayer.getUuid(), slurpPlayer.getSession().getUuid(), true, false);
            SlurpEntry.create(giveableUpdateEntry, slurpPlayer.getSession().getToken()).get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SlurpMessageException) {
                player.sendMessage(TextBuilder.error(cause.getMessage()));
            }
            else {
                player.sendMessage(TextBuilder.error("Something went wrong!"));
            }
        }
        catch (Exception e) {
            player.sendMessage(TextBuilder.error("Something went wrong!"));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);
        int balance = slurpPlayer.getGiveable().getSips();

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
            for (int i = 1; i <= balance; i++) {
                amounts.add(String.valueOf(i));
            }
            return amounts;
        }

        return null;
    }
}