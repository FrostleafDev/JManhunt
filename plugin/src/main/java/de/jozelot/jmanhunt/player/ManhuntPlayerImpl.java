package de.jozelot.jmanhunt.player;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.event.ManhuntTeamAssignEvent;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import de.jozelot.jmanhunt.api.player.Sound;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ManhuntPlayerImpl implements ManhuntPlayer {

    private final JManhunt plugin;

    private ManhuntTeam team;
    private final UUID uuid;
    private boolean alive;
    private int lives;
    private int kills;
    private int deaths;

    public ManhuntPlayerImpl(UUID uuid, ManhuntTeam team, int kills, int deaths, int lives, boolean alive, JManhunt plugin) {
        this.uuid = uuid;
        this.team = team;
        this.kills = kills;
        this.deaths = deaths;
        this.lives = lives;
        this.alive = alive;
        this.plugin = plugin;
    }

    public ManhuntPlayerImpl(UUID uuid, JManhunt plugin) {
        this.uuid = uuid;
        this.team = ManhuntTeam.NONE;
        this.alive = true;
        this.lives = 1;
        this.kills = 0;
        this.deaths = 0;
        this.plugin = plugin;
    }

    @Override
    public ManhuntTeam getTeam() {
        return team;
    }

    @Override
    public void setTeam(ManhuntTeam team) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            ManhuntTeamAssignEvent event = new ManhuntTeamAssignEvent(this, this.team, team);
            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }

            this.team = event.getNewTeam();
        });
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }


    @Override
    public boolean isEliminated() {
        return !alive;
    }

    @Override
    public void eliminate() {
        if (!alive) return;

        // TODO
        if (!(team == ManhuntTeam.RUNNER)) return;
        alive = false;
    }

    @Override
    public void revive() {
        if (alive) return;

        // TODO
        if (!(team == ManhuntTeam.RUNNER)) return;
        alive = true;
    }

    @Override
    public int getLives() {
        return lives;
    }

    @Override
    public void setLives(int lives) {
        if (!(team == ManhuntTeam.RUNNER)) return;
        if (this.lives == 0) {
            revive();
        }
        this.lives = lives;
    }

    @Override
    public int getKills() {
        return kills;
    }

    @Override
    public int addKill() {
        return ++kills;
    }

    @Override
    public int removeKill() {
        return --kills;
    }

    @Override
    public void setKills(int kills) {
        this.kills = kills;
    }

    @Override
    public int getDeaths() {
        return deaths;
    }

    @Override
    public int addDeath() {
        return ++deaths;
    }

    @Override
    public int removeDeath() {
        return --deaths;
    }

    @Override
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    @Override
    public void playSound(@NotNull Sound sound) {
        var sounds = plugin.getBootstrap().getConfigManager().getSounds();
        String soundKey;

        switch (sound) {
            case SUCCESS -> soundKey = sounds.getSuccess();
            case ERROR -> soundKey = sounds.getError();
            case NOTIFY -> soundKey = sounds.getNotify();
            case EXPERIENCE -> soundKey = sounds.getExperience();
            case WARNING -> soundKey = sounds.getWarning();
            case PLING -> soundKey = sounds.getPling();
            case null, default -> soundKey = sounds.getPling();
        }

        try {
            org.bukkit.Sound bukkitSound = org.bukkit.Sound.valueOf(soundKey.toUpperCase());

            getPlayer().playSound(getPlayer().getLocation(), bukkitSound, SoundCategory.UI, 1.0f, 1.0f);

        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound name in config: " + soundKey);

            getPlayer().playSound(getPlayer().getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.UI, 1.0f, 1.0f);
        }
    }
}
