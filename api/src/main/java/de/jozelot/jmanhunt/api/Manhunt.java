package de.jozelot.jmanhunt.api;

import de.jozelot.jmanhunt.api.game.GameManager;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;

import java.util.UUID;

public interface Manhunt {

    GameManager getGameManager();

    ManhuntPlayer getPlayer(UUID uuid);
}
