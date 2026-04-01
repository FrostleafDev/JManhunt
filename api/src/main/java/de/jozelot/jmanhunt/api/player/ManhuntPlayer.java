package de.jozelot.jmanhunt.api.player;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface ManhuntPlayer {

    ManhuntTeam getTeam();
    void setTeam(ManhuntTeam team);

    UUID getUniqueId();
    Player getPlayer();
}
