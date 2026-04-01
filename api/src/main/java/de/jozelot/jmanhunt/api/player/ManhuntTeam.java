package de.jozelot.jmanhunt.api.player;

public enum ManhuntTeam {
    HUNTER,
    RUNNER,
    SPECTATOR,
    NONE;

    public boolean isActive() {
        return this == HUNTER || this == RUNNER;
    }
}
