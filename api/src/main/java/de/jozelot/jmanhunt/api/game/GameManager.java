package de.jozelot.jmanhunt.api.game;

import de.jozelot.jmanhunt.api.event.GameStateChangeEvent;
import de.jozelot.jmanhunt.api.game.timer.ManhuntTimer;
import org.jetbrains.annotations.NotNull;

public interface GameManager {

    /**
     * Returns the current gamestate
     * @return The {@link GameState} in which the plugin currently is
     */
    @NotNull
    GameState getGameState();

    /**
     * Change the game state by you're liking. Will result in the {@link GameStateChangeEvent} event
     * @param state The {@link GameState} to set the game
     */
    void setGameState(@NotNull GameState state);

    PhaseManager getPhaseManager();
    ManhuntTimer getTimer();
}
