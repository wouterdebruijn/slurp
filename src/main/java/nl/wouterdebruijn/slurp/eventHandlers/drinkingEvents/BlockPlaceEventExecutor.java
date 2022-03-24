package nl.wouterdebruijn.slurp.eventHandlers.drinkingEvents;

import nl.wouterdebruijn.slurp.controller.MessageController;
import nl.wouterdebruijn.slurp.eventHandlers.ChanceExecutor;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Arrays;

abstract public class BlockPlaceEventExecutor extends ChanceExecutor<BlockPlaceEvent> {

    protected final Material[] materials;

    public BlockPlaceEventExecutor(double chance, Material[] materials) {
        super(chance);
        this.materials = materials;
    }

    @Override
    protected boolean beforeExecution(BlockPlaceEvent event) {
        return Arrays.asList(materials).contains(event.getBlock().getType());
    }

    private String shotName(int shots) {
        return shots > 1 ? "shots" : "shot";
    }

    private String sipName(int sips) {
        return sips > 1 ? "sips" : "sip";
    }

    protected void broadcastPlayerDrinking(String playername, int shots, int sips, boolean giveable, String orename) {
        if (!giveable) {
            if (shots >= 1 && sips >= 1) {
                MessageController.broadcast(true, String.format("%s placed %s, now they take %d %s and %d %s!", playername, orename, shots, shotName(shots), sips, sipName(sips)));
            } else if (shots >= 1) {
                MessageController.broadcast(true, String.format("%s placed %s, now they take %d %s!", playername, orename, shots, shotName(shots)));
            } else {
                MessageController.broadcast(true, String.format("%s placed %s, now they take %d %s!", playername, orename, sips, sipName(sips)));
            }
        } else {
            if (shots >= 1 && sips >= 1) {
                MessageController.broadcast(true, String.format("%s placed %s, they received %d %s and %d %s to give away!", playername, orename, shots, shotName(shots), sips, sipName(sips)));
            } else if (shots >= 1) {
                MessageController.broadcast(true, String.format("%s placed %s, they received %d %s to give away!", playername, orename, shots, shotName(shots)));
            } else {
                MessageController.broadcast(true, String.format("%s placed %s, they received %d %s to give away!", playername, orename, sips, sipName(sips)));
            }
        }
    }

    protected void broadcastServerDrinking(String playername, int shots, int sips, boolean giveable, String orename) {
        if (!giveable) {
            if (shots >= 1 && sips >= 1) {
                MessageController.broadcast(true, String.format("%s mined %s, now everybody takes %d %s and %d %s!", playername, orename, shots, shotName(shots), sips, sipName(sips)));
            } else if (shots >= 1) {
                MessageController.broadcast(true, String.format("%s mined %s, now everybody takes %d %s!", playername, orename, shots, shotName(shots)));
            } else {
                MessageController.broadcast(true, String.format("%s mined %s, now everybody takes %d %s!", playername, orename, sips, sipName(sips)));
            }
        } else {
            if (shots >= 1 && sips >= 1) {
                MessageController.broadcast(true, String.format("%s mined %s, now everybody receives %d %s and %d %s to give away!", playername, orename, shots, shotName(shots), sips, sipName(sips)));
            } else if (shots >= 1) {
                MessageController.broadcast(true, String.format("%s mined %s, now everybody receives %d %s to give away!", playername, orename, shots, shotName(shots)));
            } else {
                MessageController.broadcast(true, String.format("%s mined %s, now everybody receives %d %s to give away!", playername, orename, sips, sipName(sips)));
            }
        }
    }
}
