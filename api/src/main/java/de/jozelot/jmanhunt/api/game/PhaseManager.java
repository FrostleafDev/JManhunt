/*
 * Copyright (c) 2026 jozelot. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api.game;

import org.jetbrains.annotations.NotNull;

/**
 * Manages the different phases of a manhunt game session.
 *
 * @author jozelot_
 * @since 1.0.0
 */
public interface PhaseManager {

    /**
     * @return true if the manhunt is currently in setup mode
     */
    boolean isSetup();

    /**
     * @return true if the manhunt is in the pre-game (waiting/lobby) phase
     */
    boolean isPreGame();

    /**
     * @return true if the manhunt is currently running
     */
    boolean isRunning();

    /**
     * @return true if the manhunt has ended
     */
    boolean isEnded();

    /**
     * Transitions the game into the setup phase.
     * Use this for initial configuration or maintenance.
     */
    void setSetup();

    /**
     * Opens the game for players and transitions to the pre-game phase.
     */
    void open();

    /**
     * Closes the pre-game phase and returns the session to setup mode.
     */
    void close();

    /**
     * Starts the manhunt and transitions to the running phase.
     */
    void start();

    /**
     * Ends the current manhunt session with a specific reason.
     *
     * @param reason the reason why the game ended
     */
    void end(@NotNull ManhuntEndReason reason);
}