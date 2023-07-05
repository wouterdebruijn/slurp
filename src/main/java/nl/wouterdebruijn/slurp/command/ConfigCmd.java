package nl.wouterdebruijn.slurp.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nl.wouterdebruijn.slurp.helper.ConfigValue;
import nl.wouterdebruijn.slurp.helper.SlurpConfig;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpSessionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConfigCmd implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("setvalue")) {
            if (args.length == 2) {
                String configValue = args[0];
                ConfigValue cValue = null;
                for (ConfigValue cv : ConfigValue.values()) {
                    if (cv.name().equalsIgnoreCase(configValue)) {
                        cValue = cv;
                    }
                }
                String numValue = args[1];
                int numVal = 0;
                try {
                    numVal = Integer.parseInt(numValue);
                } catch (NumberFormatException e) {
                    sender.sendMessage(TextBuilder.error("Second argument must be a number!"));
                }
                if (cValue == null) {
                    sender.sendMessage(TextBuilder.error("You have not entered a correct value! Hint: use tab complete"));
                } else {
                    SlurpConfig.setValue(cValue, numVal);
                    Component text = Component.text("You have succesfully saved property ", NamedTextColor.GRAY)
                            .append(Component.text(cValue.name().toLowerCase(), NamedTextColor.DARK_GRAY))
                            .append(Component.text(" with value ", NamedTextColor.GRAY))
                            .append(Component.text(numValue, NamedTextColor.DARK_GRAY))
                            .append(Component.text("!",NamedTextColor.GRAY));
                    sender.sendMessage(TextBuilder.info(text));
                }
            } else {
                sender.sendMessage(TextBuilder.info("Incorrect usage. /setvalue <value-to-set> <numeric-value>"));
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
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
