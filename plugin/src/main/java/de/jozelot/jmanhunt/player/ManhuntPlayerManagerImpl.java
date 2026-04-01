package de.jozelot.jmanhunt.player;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntPlayerManager;
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

    public ManhuntPlayerImpl createPlayer(UUID uuid) {
        ManhuntPlayerImpl player = new ManhuntPlayerImpl(uuid);
        players.put(uuid, player);

        plugin.getLogger().log(Level.INFO, "Created player object for " + Bukkit.getPlayer(uuid).getName());
        return player;
    }

    public ManhuntPlayerImpl createPlayer(Player player) {
        return createPlayer(player.getUniqueId());
    }

    public void removePlayer(UUID uuid) {
        // TODO: Save data!
        players.remove(uuid);
        plugin.getLogger().log(Level.INFO, "Removed player object for " + Bukkit.getPlayer(uuid).getName());
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
}
