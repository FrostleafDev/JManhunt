package de.jozelot.jmanhunt.game;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.event.GameStateChangeEvent;
import de.jozelot.jmanhunt.api.game.GameManager;
import de.jozelot.jmanhunt.api.game.GameState;
import de.jozelot.jmanhunt.api.game.ManhuntEndReason;
import de.jozelot.jmanhunt.api.game.PhaseManager;
import de.jozelot.jmanhunt.api.game.timer.ManhuntTimer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class GameManagerImpl implements GameManager {

    private final JManhunt plugin;

    public GameManagerImpl(JManhunt plugin) {
        this.plugin = plugin;
    }

    private GameState state;
    private ManhuntEndReason endReason = ManhuntEndReason.NONE;


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


    public void loadFromStorage() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            state = plugin.getBootstrap().getMassManager().loadState();
            endReason = plugin.getBootstrap().getMassManager().loadEndReason();
        });
    }

    public void saveToStorage() {
        final GameState currentState = this.state;
        final ManhuntEndReason currentReason = this.endReason;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getBootstrap().getMassManager().saveState(currentState);
            plugin.getBootstrap().getMassManager().saveEndReason(currentReason);
        });
    }

    public ManhuntEndReason getEndReason() {
        return endReason;
    }

    public void setEndReason(ManhuntEndReason reason) {
        endReason = reason;
    }

    @Override
    public PhaseManager getPhaseManager() {
        return plugin.getBootstrap().getPhaseManager();
    }

    @Override
    public ManhuntTimer getTimer() {
        return null;
    }
}
