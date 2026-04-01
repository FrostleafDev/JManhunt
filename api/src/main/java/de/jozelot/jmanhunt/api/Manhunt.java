package de.jozelot.jmanhunt.api;

import de.jozelot.jmanhunt.api.game.GameManager;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntPlayerManager;

import java.util.UUID;

public interface Manhunt {

    GameManager getGameManager();

    ManhuntPlayerManager getPlayerManager();

    ManhuntPlayer getPlayer(UUID uuid);
}
