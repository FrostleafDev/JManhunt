/*
 * Copyright (c) 2026 jozelot_. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api.player;

import java.util.Collection;

public interface ManhuntTeamManager {
    String getTeamNameByTeam(ManhuntTeam team);
    Collection<ManhuntPlayer> getAllPlayersFromTeam(ManhuntTeam team);
}
