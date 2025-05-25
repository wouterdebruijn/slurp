package nl.wouterdebruijn.slurp.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nl.wouterdebruijn.slurp.helper.ConfigValue;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.TextBuilder;

public class ConfigCmd implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
            String[] args) {
        if (args.length != 2)
            return false;

        String key = args[0];
        ConfigValue configValue = null;

        for (ConfigValue cv : ConfigValue.values()) {
            if (cv.name().equalsIgnoreCase(key)) {
                configValue = cv;
            }
        }

        if (configValue == null) {
            sender.sendMessage(TextBuilder.error("You have not entered a correct value! Hint: use tab complete"));
            return true;
        }

        int numberValue = 0;

        try {
            numberValue = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(TextBuilder.error("The second argument must be a number!"));
            return true;
        }

        SlurpConfig.setValue(configValue, numberValue);

        Component text = Component.text("You have succesfully saved property ", NamedTextColor.GRAY)
                .append(Component.text(configValue.name().toLowerCase(), NamedTextColor.DARK_GRAY))
                .append(Component.text(" with value ", NamedTextColor.GRAY))
                .append(Component.text(numberValue, NamedTextColor.DARK_GRAY))
                .append(Component.text("!", NamedTextColor.GRAY));
        sender.sendMessage(TextBuilder.success(text));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            return new ArrayList<>();
        }

        ArrayList<String> configValues = new ArrayList<>();
        for (ConfigValue cv : ConfigValue.values()) {
            configValues.add(cv.name());
        }
        return configValues;
    }
}
