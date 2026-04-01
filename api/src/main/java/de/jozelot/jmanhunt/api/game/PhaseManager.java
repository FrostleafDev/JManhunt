package de.jozelot.jmanhunt.api.game;

public interface PhaseManager {
    boolean isSetup();
    boolean isPreGame();
    boolean isRunning();
    boolean isEnded();

    /**
     * Sets the game to the setup phase.
     */
    void setSetup();

    /**
     * Sets the game to the pre_game phase.
     */
    void open();

    /**
     * Sets the game to the setup phase.
     */
    void close();

    /**
     * Sets the game to the running phase.
     * And starts it
     */
    void start();

    /**
     * Ends the manhunt with the given reason
     */
    void end(ManhuntEndReason reason);
}
