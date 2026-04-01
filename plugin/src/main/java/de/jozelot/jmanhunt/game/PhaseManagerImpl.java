package de.jozelot.jmanhunt.game;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.game.GameState;
import de.jozelot.jmanhunt.api.game.PhaseManager;

public class PhaseManagerImpl implements PhaseManager {

    private JManhunt plugin;

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
}
