package de.jozelot.jmanhunt.storage.mass;

import de.jozelot.jmanhunt.api.game.GameState;
import de.jozelot.jmanhunt.api.game.ManhuntEndReason;

import java.sql.Connection;
import java.sql.SQLException;

public interface ManhuntStorage {
    void init();
    void close();

    Connection getConnection() throws SQLException;
    String getPrefix();
}
