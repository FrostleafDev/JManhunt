package de.jozelot.jmanhunt.api;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.game.GameManager;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;

import java.util.UUID;

public class ManhuntImpl implements Manhunt {

    private final JManhunt plugin;

    public ManhuntImpl(JManhunt plugin) {
        this.plugin = plugin;
    }

    @Override
    public GameManager getGameManager() {
        return null;
    }

    @Override
    public ManhuntPlayer getPlayer(UUID uuid) {
        return null;
    }
}
