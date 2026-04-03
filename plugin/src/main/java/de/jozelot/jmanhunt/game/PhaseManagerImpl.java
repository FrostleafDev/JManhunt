package de.jozelot.jmanhunt.game;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.game.GameState;
import de.jozelot.jmanhunt.api.game.ManhuntEndReason;
import de.jozelot.jmanhunt.api.game.PhaseManager;
import de.jozelot.jmanhunt.api.minecraft.Weather;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import org.bukkit.WeatherType;
import org.bukkit.event.weather.WeatherChangeEvent;

public class PhaseManagerImpl implements PhaseManager {

    private final JManhunt plugin;

    public PhaseManagerImpl(JManhunt plugin) {
        this.plugin = plugin;
    }

    protected void handleStateChange(GameState state) {
        switch (state) {
            case SETUP -> {

            }
            case PRE_GAME -> {

            }
            case RUNNING -> {

            }
            case ENDED -> {

            }
        }
    }

    @Override
    public boolean isSetup() {
        return plugin.getBootstrap().getGameManager().getGameState() == GameState.SETUP;
    }

    @Override
    public boolean isPreGame() {
        return plugin.getBootstrap().getGameManager().getGameState() == GameState.PRE_GAME;
    }

    @Override
    public boolean isRunning() {
        return plugin.getBootstrap().getGameManager().getGameState() == GameState.RUNNING;
    }

    @Override
    public boolean isEnded() {
        return plugin.getBootstrap().getGameManager().getGameState() == GameState.ENDED;
    }


    @Override
    public void setSetup() {

    }

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }

    @Override
    public void start() {

    }

    @Override
    public void end(ManhuntEndReason reason) {
        plugin.getBootstrap().getGameManager().setGameState(GameState.ENDED);
        plugin.getBootstrap().getGameManager().setEndReason(reason);
    }

    public boolean canAddToTeam(ManhuntTeam team) {
        if (team == ManhuntTeam.SPECTATOR) return true;
        GameState currentState = plugin.getBootstrap().getGameManager().getGameState();
        if (currentState == GameState.SETUP || currentState == GameState.PRE_GAME) return true;
        return false;
    }

    public boolean canRemoveFromTeam(ManhuntTeam team) {
        if (team == ManhuntTeam.SPECTATOR) return true;
        GameState currentState = plugin.getBootstrap().getGameManager().getGameState();
        if (currentState == GameState.SETUP || currentState == GameState.PRE_GAME || currentState == GameState.PAUSE) return true;
        return false;
    }
}
