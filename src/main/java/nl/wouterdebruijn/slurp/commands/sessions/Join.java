package nl.wouterdebruijn.slurp.commands.sessions;

import com.google.gson.Gson;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpPlayer;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.helpers.slurp.exceptions.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.logging.Level;

public class Join implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            Player player = (Player) sender;

            try {
                SlurpPlayer slurpPlayer = SlurpPlayer.create(player, args[0]);
                player.sendMessage("Joined session with short code: " + args[0]);
                Slurp.logger.log(Level.INFO, String.format("Player %s joined session %s", player.getName(), slurpPlayer.getSession().getUuid()));
            } catch (ApiResponseException e) {
                player.sendMessage("No session found with shortcode: " + args[0]);
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
