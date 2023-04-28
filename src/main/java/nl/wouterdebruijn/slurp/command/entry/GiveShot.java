package nl.wouterdebruijn.slurp.command.entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GiveShot implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Player player = (Player) sender;
        SlurpPlayer slurpPlayer = SlurpPlayerManager.getPlayer(player);

        if (slurpPlayer == null) {
            player.sendMessage(Slurp.getPrefix().append(Component.text("You are not in a session!").color(NamedTextColor.RED)));
            return true;
        }

//        Check if player has giveables

//        Unimplemented

        SlurpEntryBuilder entry = new SlurpEntryBuilder(5, 5, slurpPlayer.getUuid(), slurpPlayer.getSession().getUuid(), false, false);
        SlurpEntry response = SlurpEntry.create(entry, slurpPlayer.getSession().getToken());
        player.sendMessage(Slurp.getPrefix().append(Component.text(gson.toJson(response))));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
