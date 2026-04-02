/*
 * Copyright (c) 2026 jozelot. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api.event;

import de.jozelot.jmanhunt.api.game.GameState;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when the global game state is about to change.
 * <p>
 * This event is cancellable. If canceled, the state transition will not occur
 * and the game remains in its previous state.
 * </p>
 *
 * @author jozelot_
 * @since 1.0.0
 */
public class GameStateChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final GameState oldState;
    private final GameState newState;
    private boolean cancelled;

    /**
     * Constructs a new GameStateChangeEvent.
     *
     * @param oldState the state the game is transitioning from
     * @param newState the state the game is transitioning to
     */
    public GameStateChangeEvent(@NotNull GameState oldState, @NotNull GameState newState) {
        this.oldState = oldState;
        this.newState = newState;
    }

    /**
     * Gets the state before the transition.
     *
     * @return the previous {@link GameState}
     */
    public @NotNull GameState getOldState() {
        return oldState;
    }

    /**
     * Gets the state that will be applied if the event is not canceled.
     *
     * @return the proposed {@link GameState}
     */
    public @NotNull GameState getNewState() {
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

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}