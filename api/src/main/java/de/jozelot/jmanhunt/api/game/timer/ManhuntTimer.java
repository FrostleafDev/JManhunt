package de.jozelot.jmanhunt.api.game.timer;

public interface ManhuntTimer {

    /**
     * @return true if the timer is currently ticking.
     */
    boolean isRunning();

    /**
     * @return The elapsed time in seconds.
     */
    long getElapsedSeconds();

    /**
     * Manually sets the elapsed time.
     * @param seconds The time in seconds.
     */
    void setElapsedSeconds(long seconds);

    /**
     * Useful for the TIME win condition.
     * @return The remaining time in seconds or -1 if no limit is set.
     */
    long getRemainingSeconds();

    /**
     * Formats the time into a human-readable string.
     * @param pattern The pattern (e.g., "HH:mm:ss" or "mm:ss").
     * @return The formatted time string.
     */
    String format(String pattern);

    /**
     * Default formatting that automatically switches between mm:ss and HH:mm:ss.
     * @return The auto-formatted time.
     */
    default String formatAuto() {
        long s = getElapsedSeconds();
        if (s < 3600) {
            return String.format("%02d:%02d", (s % 3600) / 60, s % 60);
        }
        return String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60);
    }
}
