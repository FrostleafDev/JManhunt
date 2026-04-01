package de.jozelot.jmanhunt.game;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.event.GameStateChangeEvent;
import de.jozelot.jmanhunt.api.game.GameManager;
import de.jozelot.jmanhunt.api.game.GameState;
import de.jozelot.jmanhunt.api.game.PhaseManager;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class GameManagerImpl implements GameManager {

    private final JManhunt plugin;

    public GameManagerImpl(JManhunt plugin) {
        this.plugin = plugin;
    }

    private GameState state;


    @NotNull
    @Override
    public GameState getGameState() {
        return state;
    }


    @Override
    public void setGameState(@NotNull GameState state) {
        GameStateChangeEvent event = new GameStateChangeEvent(this.state, state);

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            plugin.getLogger().info("Game state change to " + state + " was canceled");
            return;
        }

        this.state = state;

        plugin.getBootstrap().getPhaseManager().handleStateChange(state);
    }


    /**
     * TODO
     */
    public void loadFromStorage() {

    }

    /**
     * TODO
     */
    public void saveToStorage() {

    }

    @Override
    public PhaseManager getPhaseManager() {
        return plugin.getBootstrap().getPhaseManager();
    }
}
