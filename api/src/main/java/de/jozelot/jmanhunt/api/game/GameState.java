/*
 * Copyright (c) 2026 jozelot. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api.game;

/**
 * Represents the various states a manhunt game session can be in.
 *
 * @author jozelot_
 * @since 1.0.0
 */
public enum GameState {

    /**
     * The game is in configuration mode.
     * Access is restricted to administrators to define settings and locations.
     */
    SETUP,

    /**
     * The lobby phase before the game starts.
     * Players can join teams and prepare for the match.
     */
    PRE_GAME,

    /**
     * The manhunt is actively running.
     * All game mechanics, timers, and win conditions are enabled.
     */
    RUNNING,

    /**
     * The game is temporarily suspended.
     * This occurs during administrative intervention or when a critical player disconnects.
     */
    PAUSE,

    /**
     * The game has concluded.
     * Win conditions have been met or the session was manually terminated.
     */
    ENDED;

    /**
     * @return true if the game is currently in a state where players are actively playing.
     */
    public boolean isInProgress() {
        return this == RUNNING || this == PAUSE;
    }
}