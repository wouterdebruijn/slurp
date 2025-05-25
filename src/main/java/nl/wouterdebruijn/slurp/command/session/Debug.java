package nl.wouterdebruijn.slurp.command.session;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Debug implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {

        // ArrayList<EventLog> eventLogs = Slurp.aiHandlerEvent.getEventLogs();

        // if (eventLogs.isEmpty()) {
        // sender.sendMessage("No events logged yet.");
        // return true;
        // }

        // JsonArray jsonArray = new JsonArray();
        // for (EventLog eventLog : eventLogs) {
        // jsonArray.add(eventLog.toJson());
        // }

        // Player player = (Player) sender;

        // SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        // if (args.length > 0 && args[0].equalsIgnoreCase("generate")) {

        // } else {
        // sender.sendMessage("Use /debug generate to generate actions.");
        // }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
