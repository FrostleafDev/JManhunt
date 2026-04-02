/*
 * Copyright (c) 2026 jozelot. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api.game;

/**
 * Represents the various reasons why a manhunt game session has ended.
 *
 * @author jozelot_
 * @since 1.0.0
 */
public enum ManhuntEndReason {

    /**
     * The runners have achieved their goal and won the game.
     */
    RUNNER_WIN,

    /**
     * The hunters have successfully eliminated the runners.
     */
    HUNTER_WIN,

    /**
     * The game ended in a draw, typically due to a timeout or mutual elimination.
     */
    DRAW,

    /**
     * The manhunt was manually canceled by an administrator or a system error.
     */
    MANHUNT_CANCELED,

    /**
     * No specific end reason; the game is still active or in an undefined state.
     */
    NONE;

    /**
     * @return true if the reason represents an actual victory for any team.
     */
    public boolean isVictory() {
        return this == RUNNER_WIN || this == HUNTER_WIN;
    }
}