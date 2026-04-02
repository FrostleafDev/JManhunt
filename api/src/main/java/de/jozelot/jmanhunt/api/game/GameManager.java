/*
 * Copyright (c) 2026 jozelot. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api.game;

import de.jozelot.jmanhunt.api.event.GameStateChangeEvent;
import de.jozelot.jmanhunt.api.game.timer.ManhuntTimer;
import org.jetbrains.annotations.NotNull;

/**
 * The central manager for controlling the manhunt game flow and its sub-systems.
 *
 * @author jozelot_
 * @since 1.0.0
 */
public interface GameManager {

    /**
     * Gets the current state of the game.
     *
     * @return the current {@link GameState}
     */
    @NotNull
    GameState getGameState();

    /**
     * Updates the game state.
     * <p>
     * Changing the state will trigger a {@link GameStateChangeEvent}.
     * </p>
     *
     * @param state the new {@link GameState} to apply
     */
    void setGameState(@NotNull GameState state);

    /**
     * Gets the phase manager used to transition between different game stages.
     *
     * @return the {@link PhaseManager} instance
     */
    @NotNull
    PhaseManager getPhaseManager();

    /**
     * Gets the timer responsible for tracking the game duration and intervals.
     *
     * @return the {@link ManhuntTimer} instance
     */
    @NotNull
    ManhuntTimer getTimer();
}