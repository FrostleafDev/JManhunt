package de.jozelot.jmanhunt.player;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntPlayerManager;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Level;

public class ManhuntPlayerManagerImpl implements ManhuntPlayerManager {

    private final JManhunt plugin;

    public ManhuntPlayerManagerImpl(JManhunt plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, ManhuntPlayerImpl> players = new HashMap<>();

    /**
     * This is the main method for registering a new {@link ManhuntPlayer}.
     * @param uuid
     * @return The new {@link ManhuntPlayer} Objekt
     */
    public ManhuntPlayerImpl createPlayer(UUID uuid) {
        ManhuntPlayerImpl player = new ManhuntPlayerImpl(uuid);
        players.put(uuid, player);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getBootstrap().getMassManager().loadManhuntPlayer(player);
        });

        plugin.getLogger().log(Level.INFO, "Created player object for " + Bukkit.getPlayer(uuid).getName());
        return player;
    }

    /**
     * Just another way to create it via the {@link Player}
     * @param player
     * @return
     */
    public ManhuntPlayerImpl createPlayer(Player player) {
        return createPlayer(player.getUniqueId());
    }

    /**
     * Removes a player from the server and saves it data to the database
     * This is only called when the player leaves the server
     * @param uuid
     */
    public void removePlayer(UUID uuid) {
        ManhuntPlayerImpl player = players.remove(uuid);
        String playerName = player.getPlayer().getName();

        if (player != null) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                plugin.getBootstrap().getMassManager().saveManhuntPlayer(player);
            });

            plugin.getLogger().info("Saved and removed player object for: " + playerName);
        }
    }

    /**
     * Here all current {@link ManhuntPlayer} Objects are saved.
     * This only gets run in a reload and shutdown
     */
    public void saveAllToStorage() {
        Collection<ManhuntPlayerImpl> toSave = players.values();
        plugin.getBootstrap().getMassManager().saveAllPlayers(new ArrayList<>(toSave));
        players.clear();
    }

    /**
     * Here all current {@link ManhuntPlayer} Objects are loaded
     * This only gets run in a reload
     */
    public void loadAllFromStorage() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            createPlayer(player);
        }
    }

    public void removePlayer(Player player) {
        removePlayer(player.getUniqueId());
    }

    public void removePlayer(ManhuntPlayerImpl player) {
        removePlayer(player.getUniqueId());
    }

    @Override
    public ManhuntPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    @Override
    public ManhuntPlayer getPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    @Override
    public Collection<ManhuntPlayer> getPlayers() {
        return new ArrayList<>(players.values());
    }

    @Override
    public Collection<ManhuntPlayer> getActiveParticipants() {
        return getPlayers().stream().filter(p -> p.getTeam().isActive()).toList();
    }

    @Override
    public Collection<ManhuntPlayer> getRunners() {
        return getPlayers().stream().filter(p -> p.getTeam() == ManhuntTeam.RUNNER).toList();
    }

    @Override
    public Collection<ManhuntPlayer> getHunters() {
        return getPlayers().stream().filter(p -> p.getTeam() == ManhuntTeam.HUNTER).toList();
    }

    @Override
    public Collection<ManhuntPlayer> getSpectators() {
        return getPlayers().stream().filter(p -> p.getTeam() == ManhuntTeam.SPECTATOR).toList();
    }

    @Override
    public Collection<ManhuntPlayer> getPlayersWithoutTeam() {
        return getPlayers().stream().filter(p -> p.getTeam() == ManhuntTeam.NONE).toList();
    }
}
