package de.jozelot.jmanhunt.player;

import de.jozelot.jmanhunt.api.event.ManhuntTeamAssignEvent;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ManhuntPlayerImpl implements ManhuntPlayer {

    private ManhuntTeam team;
    private final UUID uuid;

    public ManhuntPlayerImpl(UUID uuid, ManhuntTeam team) {
        this.uuid = uuid;
        this.team = team;
    }

    public ManhuntPlayerImpl(UUID uuid) {
        this.uuid = uuid;
        this.team = ManhuntTeam.NONE;
    }

    @Override
    public ManhuntTeam getTeam() {
        return team;
    }

    @Override
    public void setTeam(ManhuntTeam team) {
        ManhuntTeamAssignEvent event = new ManhuntTeamAssignEvent(this, this.team, team);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }
        this.team = event.getNewTeam();
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
