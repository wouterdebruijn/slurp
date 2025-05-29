package nl.wouterdebruijn.slurp.command.session;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;

public class Debug implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {

        // Read all provided text and set the setAdditionalInstructions

        StringBuilder instructions = new StringBuilder();
        for (String arg : args) {
            instructions.append(arg).append(" ");
        }
        String additionalInstructions = instructions.toString().trim();

        SlurpConfig.setAdditionalInstructions(additionalInstructions);
        Slurp.plugin.getLogger().info("Set additional instructions for Google AI to: " + additionalInstructions);

        // Send a confirmation message to the sender
        sender.sendMessage(SlurpConfig.prefix() + "Additional instructions for Google AI have been set");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
