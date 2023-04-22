package nl.wouterdebruijn.slurp.commands.sessions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpPlayer;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpSession;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpSessionManager;
import nl.wouterdebruijn.slurp.helpers.slurp.exceptions.ApiResponseException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.logging.Level;

public class Debug implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

//        Print all current sessions
        player.sendMessage("Current players:");
        for (SlurpPlayer slurpPlayer : SlurpPlayerManager.dump()) {
            player.sendMessage(gson.toJson(slurpPlayer));
            player.sendMessage("-------------");
        }

        player.sendMessage("Current sessions:");
        for (SlurpSession slurpPlayer : SlurpSessionManager.dump()) {
            player.sendMessage(gson.toJson(slurpPlayer));
            player.sendMessage("-------------");
        }

        Slurp.logger.log(Level.INFO, "Current players:");
        for (SlurpPlayer slurpPlayer : SlurpPlayerManager.dump()) {
            Slurp.logger.log(Level.INFO, gson.toJson(slurpPlayer));
            Slurp.logger.log(Level.INFO, "-------------");
        }

        Slurp.logger.log(Level.INFO, "Current sessions:");
        for (SlurpSession slurpPlayer : SlurpSessionManager.dump()) {
            Slurp.logger.log(Level.INFO, gson.toJson(slurpPlayer));
            Slurp.logger.log(Level.INFO, "-------------");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
