package de.jozelot.jmanhunt.api.player;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ManhuntPlayerManager {

    ManhuntPlayer getPlayer(UUID uuid);
    ManhuntPlayer getPlayer(Player player);

    Collection<ManhuntPlayer> getPlayers();

    Collection<ManhuntPlayer> getActiveParticipants();
    Collection<ManhuntPlayer> getRunners();
    Collection<ManhuntPlayer> getHunters();
    Collection<ManhuntPlayer> getSpectators();
    Collection<ManhuntPlayer> getPlayersWithoutTeam();
}
