/*
 * Copyright (c) 2026 jozelot. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api;

import de.jozelot.jmanhunt.api.game.GameManager;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntPlayerManager;
import de.jozelot.jmanhunt.api.player.ManhuntTeamManager;
import de.jozelot.jmanhunt.api.game.timer.ManhuntTimerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The main entry point for the JManhunt API.
 * <p>
 * This interface provides access to all sub-managers and utility methods
 * required to interact with the manhunt game logic.
 * </p>
 *
 * @author jozelot_
 * @since 1.0.0
 */
public interface Manhunt {

    /**
     * Gets the manager responsible for game states and world logic.
     *
     * @return the game manager instance
     */
    @NotNull
    GameManager getGameManager();

    /**
     * Gets the manager responsible for player data and team assignments.
     *
     * @return the player manager instance
     */
    @NotNull
    ManhuntPlayerManager getPlayerManager();

    /**
     * Shorthand method to get a {@link ManhuntPlayer} by their unique ID.
     * <p>
     * This is a utility method that delegates to {@link ManhuntPlayerManager#getPlayer(UUID)}.
     * </p>
     *
     * @param uuid the UUID of the player to retrieve
     * @return the manhunt player, or {@code null} if not found
     */
    @Nullable
    ManhuntPlayer getPlayer(UUID uuid);

    /**
     * Gets the manager responsible for team focused actions
     * @return
     */
    ManhuntTeamManager getTeamManager();

    /**
     * Gets the manager responsible for the timer focused action
     * @return
     */
    ManhuntTimerManager getTimerManager();

    /**
     * Is the plugin running in debug mode?
     * With this check you can secure your addon to do specific things based on that
     * @return The current state
     */
    boolean isDebugMode();
}
