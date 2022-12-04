package nl.wouterdebruijn.slurp;

import nl.wouterdebruijn.slurp.commands.sessions.Create;
import nl.wouterdebruijn.slurp.commands.sessions.Join;
import nl.wouterdebruijn.slurp.helpers.SlurpConfig;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpPlayer;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.helpers.slurp.SlurpSession;
import nl.wouterdebruijn.slurp.helpers.slurp.exceptions.ApiException;
import nl.wouterdebruijn.slurp.helpers.slurp.exceptions.MissingSessionException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.kerberos.KerberosTicket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Slurp extends JavaPlugin {
    public static Plugin plugin = null;
    public static Logger logger = null;


    @Override
    public void onDisable() {
        SlurpPlayerManager.saveToDisk();
    }

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();

        logger.setLevel(Level.ALL);

        SlurpPlayerManager.loadFromDisk();

        SlurpConfig.initialize();

//      Register commands
        Objects.requireNonNull(getCommand("join")).setExecutor(new Join());
        Objects.requireNonNull(getCommand("create")).setExecutor(new Create());
        Objects.requireNonNull(getCommand("debug")).setExecutor(new nl.wouterdebruijn.slurp.commands.sessions.Debug());

    }
}
