package de.jozelot.jmanhunt.player;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import de.jozelot.jmanhunt.api.player.ManhuntTeamManager;
import de.jozelot.jmanhunt.storage.PlayerCount;

import java.util.Collection;
import java.util.logging.Level;

public class ManhuntTeamManagerImpl implements ManhuntTeamManager {

    private final JManhunt plugin;

    public ManhuntTeamManagerImpl(JManhunt plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getTeamNameByTeam(ManhuntTeam team) {
        return plugin.getBootstrap().getLangManager().getLanguageConfig().getString("teams." + team.name().toLowerCase());
    }

    @Override
    public Collection<ManhuntPlayer> getAllPlayersFromTeam(ManhuntTeam team) {
        return plugin.getBootstrap().getManhuntPlayerManager().getPlayers().stream().filter(p -> p.getTeam() == team).toList();
    }
}
