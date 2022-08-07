package nl.wouterdebruijn.slurp.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.api.SlurpAPI;
import nl.wouterdebruijn.slurp.controller.LogController;
import nl.wouterdebruijn.slurp.controller.MessageController;
import nl.wouterdebruijn.slurp.entity.SessionPlayerBody;
import nl.wouterdebruijn.slurp.entity.SlurpSession;
import nl.wouterdebruijn.slurp.exceptions.APIPostException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Session implements TabExecutor {
    public Session() {
        Objects.requireNonNull(Slurp.getPlugin().getCommand("session")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (args[0].equals("create")) {
            try {
                HttpResponse<String> httpResponse = SlurpAPI.post("/session", new Object());

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                SlurpSession session = gson.fromJson(httpResponse.body(), SlurpSession.class);
                MessageController.sendMessage(player, true, "Created new session, short id: ", session.sessionShort);

                return true;
            } catch (Exception e) {
                MessageController.sendMessage(player, true, "Could not create a new session.");
                e.printStackTrace();
            }
        } else {
            try {
                HttpResponse<String> httpResponse = SlurpAPI.post("/player", new SessionPlayerBody(args[1], sender.getName()));
                MessageController.sendMessage(player, true, httpResponse.body());
                return true;
            } catch (Exception e) {
                MessageController.sendMessage(player, true, "Could not join session.");
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return null;
        }

        return new ArrayList<>(List.of(new String[]{"create", "join"}));
    }
}
