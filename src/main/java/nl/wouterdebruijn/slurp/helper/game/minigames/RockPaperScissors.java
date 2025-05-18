package nl.wouterdebruijn.slurp.helper.game.minigames;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import nl.wouterdebruijn.slurp.Slurp;
import nl.wouterdebruijn.slurp.helper.TextBuilder;
import nl.wouterdebruijn.slurp.helper.game.api.SlurpEntryBuilder;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpPlayer;
import nl.wouterdebruijn.slurp.helper.game.entity.SlurpSession;
import nl.wouterdebruijn.slurp.helper.game.handlers.TitleHandler;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpPlayerManager;
import nl.wouterdebruijn.slurp.helper.game.manager.SlurpSessionManager;

public class RockPaperScissors {
    /**
     * Rock paper scissors minigame to be played by two players.
     */
    private final Participant player1;
    private final Participant player2;
    private int stakes = 1; // The amount of sips the loser has to take.

    public RockPaperScissors(Player p1, Player p2) {
        this.player1 = new Participant(p1, this);
        this.player2 = new Participant(p2, this);
    }

    public int getStakes() {
        return this.stakes;
    }

    public void setStakes(int stakes) {
        this.stakes = stakes;
    }

    /**
     * Up the stakes by a factor of 2.
     */
    public void upStakes() {
        this.stakes *= 2;
    }

    public Participant getParticipant(Player player) {
        if (this.player1.getPlayer() == player) {
            return this.player1;
        } else if (this.player2.getPlayer() == player) {
            return this.player2;
        } else {
            return null;
        }
    }

    public Participant getOpponent(Participant player) {
        return player == this.player1 ? this.player2 : this.player1;
    }

    /**
     * Play a round of rock paper scissors.
     */
    public Participant getWinner() {
        Move move1 = this.player1.getMove();
        Move move2 = this.player2.getMove();

        if (move1 == move2) {
            // Draw
            return null;
        } else if (move1 == Move.ROCK && move2 == Move.SCISSORS) {
            // Player 1 wins
            return this.player1;
        } else if (move1 == Move.PAPER && move2 == Move.ROCK) {
            // Player 1 wins
            return this.player1;
        } else if (move1 == Move.SCISSORS && move2 == Move.PAPER) {
            // Player 1 wins
            return this.player1;
        } else {
            // Player 2 wins
            return this.player2;
        }
    }

    public void startRound() {
        Bukkit.getScheduler().runTaskAsynchronously(Slurp.plugin, () -> {
            // Ask both players for their move asynchronously
            CompletableFuture<Void> p1 = CompletableFuture.runAsync(this.player1::askMove);
            CompletableFuture<Void> p2 = CompletableFuture.runAsync(this.player2::askMove);

            // Wait for both players to have made their move
            CompletableFuture.allOf(p1, p2).join();

            Participant winner = this.getWinner();
            if (winner == null) {
                // Draw
                CompletableFuture<Void> t1 = TitleHandler.asyncTitle(this.player1.getPlayer(),
                        Title.title(Component.text("Draw").color(NamedTextColor.YELLOW), Component.empty()));
                CompletableFuture<Void> t2 = TitleHandler.asyncTitle(this.player2.getPlayer(),
                        Title.title(Component.text("Draw").color(NamedTextColor.YELLOW), Component.empty()));

                // Wait for both titles to be displayed
                CompletableFuture.allOf(t1, t2).join();

                // Start a new round
                this.startRound();
            } else {
                Participant loser = this.getOpponent(winner);
                // Winner found
                CompletableFuture<Void> t1 = TitleHandler.asyncTitle(winner.getPlayer(),
                        Title.title(Component.text("You won!").color(NamedTextColor.GREEN), Component.empty()));
                CompletableFuture<Void> t2 = TitleHandler.asyncTitle(loser.getPlayer(),
                        Title.title(Component.text("You lost!").color(NamedTextColor.RED), Component.empty()));

                // Wait for both titles to be displayed
                CompletableFuture.allOf(t1, t2).join();

                // Ask for a rematch
                CompletableFuture<Boolean> winnerRematch = CompletableFuture.supplyAsync(winner::askRematch);
                CompletableFuture<Boolean> loserRematch = CompletableFuture.supplyAsync(loser::askRematch);

                try {
                    CompletableFuture.allOf(winnerRematch, loserRematch).join();

                    if (winnerRematch.get() && loserRematch.get()) {
                        // Both players want a rematch
                        this.upStakes();
                        this.startRound();
                    } else {
                        SlurpPlayer loserSlurpPlayer = SlurpPlayerManager.getPlayer(loser.getPlayer());

                        if (loserSlurpPlayer == null) {
                            Slurp.logger.severe("Error while getting loser slurp player");
                            return;
                        }

                        SlurpSession serverSession = SlurpSessionManager
                                .getSession(loserSlurpPlayer.getSession().getUuid());

                        if (serverSession == null) {
                            Slurp.logger.severe("Error while getting server session");
                            return;
                        }

                        // Add the stakes to the loser's inventory
                        SlurpEntryBuilder entry = new SlurpEntryBuilder(this.getStakes(), loserSlurpPlayer.getUuid(),
                                serverSession.getUuid(), false, false);
                        SlurpEntry.create(entry);

                        Bukkit.broadcast(TextBuilder.success(
                                String.format("%s played rock paper scissors with %s and lost, taking %d sips.",
                                        loser.getPlayer().getName(), winner.getPlayer().getName(), this.getStakes())));
                    }

                } catch (Exception e) {
                    Slurp.logger.severe("Error while getting rematch");
                    e.printStackTrace();
                }
            }
        });
    }

    private enum Move {
        ROCK,
        PAPER,
        SCISSORS
    }

    private static class Participant {
        private final Player player;
        private final RockPaperScissors game;
        private Move move;

        public Participant(Player player, RockPaperScissors game) {
            this.player = player;
            this.game = game;
        }

        public Move getMove() {
            return this.move;
        }

        public void setMove(Move move) {
            this.move = move;
        }

        public Player getPlayer() {
            return this.player;
        }

        public void askMove() {
            Inventory inv = Bukkit.createInventory(null, 27, Component.text("Rock paper scissors"));

            inv.setItem(10, new ItemStack(Material.STONE));
            inv.setItem(13, new ItemStack(Material.PAPER));
            inv.setItem(16, new ItemStack(Material.SHEARS));

            GameActionListener moveListener = new GameActionListener(this, inv);

            Bukkit.getScheduler().runTask(Slurp.plugin, () -> {
                Bukkit.getPluginManager().registerEvents(moveListener, Slurp.plugin);
                this.player.openInventory(inv);
            });

            try {
                int move = moveListener.getClickedSlot().get().intValue();

                switch (move) {
                    case 10 -> this.setMove(Move.ROCK);
                    case 13 -> this.setMove(Move.PAPER);
                    case 16 -> this.setMove(Move.SCISSORS);
                }
            } catch (Exception e) {
                Slurp.logger.severe("Error while getting clicked slot");
                e.printStackTrace();
            }
        }

        public boolean askRematch() {
            Inventory inv = Bukkit.createInventory(null, 27,
                    Component.text(String.format("Rematch for double the stakes (%d)?", this.game.getStakes() * 2)));

            ItemStack yes = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
            yes.editMeta(meta -> {
                meta.displayName(Component.text("Yes"));
            });
            inv.setItem(10, yes);

            ItemStack no = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            no.editMeta(meta -> {
                meta.displayName(Component.text("No"));
            });
            inv.setItem(16, no);

            GameActionListener rematchListener = new GameActionListener(this, inv);

            Bukkit.getScheduler().runTask(Slurp.plugin, () -> {
                Bukkit.getPluginManager().registerEvents(rematchListener, Slurp.plugin);
                this.player.openInventory(inv);
            });

            try {
                int move = rematchListener.getClickedSlot().get().intValue();

                switch (move) {
                    case 10 -> {
                        return true;
                    }
                    case 16 -> {
                        return false;
                    }
                }
            } catch (Exception e) {
                Slurp.logger.severe("Error while getting clicked slot");
                e.printStackTrace();
            }
            return false;
        }

        static class GameActionListener implements Listener {
            /**
             * GameActionListener listener for one participant of the game.
             */
            private final Participant participant;
            private final Inventory inventory;
            private final CompletableFuture<Number> clickedSlot = new CompletableFuture<>();

            public GameActionListener(Participant participant, Inventory inventory) {
                this.participant = participant;
                this.inventory = inventory;
            }

            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                // Check if the inventory is the same as the one we created
                if (!Objects.equals(event.getClickedInventory(), this.inventory)) {
                    return;
                }

                Player p = (Player) event.getWhoClicked();

                // Check if the player is the same as the one we created the listener for
                if (p != this.participant.getPlayer()) {
                    return;
                }

                // Cancel the player clicking in the inventory
                event.setCancelled(true);

                // Check if the player clicked on an item
                if (event.getCurrentItem() == null) {
                    return;
                }

                clickedSlot.complete(event.getSlot());
                event.getInventory().close();
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

                // Remove the listener
                InventoryClickEvent.getHandlerList().unregister(this);
            }

            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                // Check if the inventory is the same as the one we created
                if (!Objects.equals(event.getInventory(), this.inventory)) {
                    return;
                }

                // Check if the player is the same as the one we created the listener for
                if (event.getPlayer() != this.participant.getPlayer()) {
                    return;
                }

                // The player closed the inventory without clicking on an item
                if (!this.clickedSlot.isDone()) {
                    // Open the inventory again after 5 ticks
                    Bukkit.getScheduler().runTaskLater(Slurp.plugin, () -> {
                        event.getPlayer().openInventory(inventory);
                    }, 5);
                }

            }

            public CompletableFuture<Number> getClickedSlot() {
                return this.clickedSlot;
            }
        }
    }
}
