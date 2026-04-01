package de.jozelot.jmanhunt.api.event;

import de.jozelot.jmanhunt.api.game.GameState;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameStateChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final GameState oldState;
    private final GameState newState;
    private boolean cancelled;

    public GameStateChangeEvent(GameState oldState, GameState newState) {
        this.oldState = oldState;
        this.newState = newState;
    }

    public GameState getOldState() {
        return oldState;
    }

    public GameState getNewState() {
        return newState;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}