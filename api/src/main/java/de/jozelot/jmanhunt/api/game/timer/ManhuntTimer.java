/*
 * Copyright (c) 2026 jozelot. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api.game.timer;

import org.jetbrains.annotations.NotNull;

/**
 * Interface for managing and retrieving time-related data during a manhunt session.
 *
 * @author jozelot_
 * @since 1.0.0
 */
public interface ManhuntTimer {

    /**
     * Checks if the timer is currently active and counting.
     *
     * @return true if the timer is ticking; false otherwise
     */
    boolean isRunning();

    /**
     * Gets the total time that has passed since the timer started.
     *
     * @return the elapsed time in seconds
     */
    long getElapsedSeconds();

    /**
     * Manually overrides the current elapsed time.
     *
     * @param seconds the new elapsed time in seconds
     */
    void setElapsedSeconds(long seconds);

    /**
     * Calculates the time left until a potential time-based win condition is met.
     *
     * @return the remaining time in seconds, or -1 if no time limit has been configured
     */
    long getRemainingSeconds();

    /**
     * Formats the current elapsed time into a human-readable string using a custom pattern.
     *
     * @param pattern the formatting pattern (e.g., "HH:mm:ss")
     * @return the formatted time string
     */
    @NotNull
    String format(@NotNull String pattern);

    /**
     * Provides a default human-readable representation of the elapsed time.
     * <p>
     * Automatically switches between "mm:ss" and "HH:mm:ss" based on the total duration.
     * </p>
     *
     * @return the auto-formatted time string
     */
    @NotNull
    default String formatAuto() {
        long s = getElapsedSeconds();
        if (s < 3600) {
            return String.format("%02d:%02d", (s % 3600) / 60, s % 60);
        }
        return String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60);
    }
}