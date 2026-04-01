package de.jozelot.jmanhunt.api.game;

public interface PhaseManager {
    boolean isSetup();
    boolean isPreGame();
    boolean isRunning();
    boolean isEnded();
}
