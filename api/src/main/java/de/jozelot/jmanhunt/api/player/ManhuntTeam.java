/*
 * Copyright (c) 2026 jozelot. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api.player;

/**
 * Represents the different teams a player can be assigned to during a manhunt.
 *
 * @author jozelot_
 * @since 1.0.0
 */
public enum ManhuntTeam {

    /**
     * Players who are hunting the runners.
     */
    HUNTER,

    /**
     * Players who are being hunted and must reach the objective.
     */
    RUNNER,

    /**
     * Players who are observing the game and do not participate in the logic.
     */
    SPECTATOR,

    /**
     * Players who have not been assigned to a team yet (e.g., during setup).
     */
    NONE;

    /**
     * Checks if the team is actively participating in the manhunt gameplay.
     * <p>
     * Active teams are {@link #HUNTER} and {@link #RUNNER}.
     * </p>
     *
     * @return true if the team is a runner or a hunter
     */
    public boolean isActive() {
        return this == HUNTER || this == RUNNER;
    }
}
