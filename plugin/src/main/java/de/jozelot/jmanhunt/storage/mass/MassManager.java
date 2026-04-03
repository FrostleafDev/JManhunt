package de.jozelot.jmanhunt.storage.mass;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.game.GameState;
import de.jozelot.jmanhunt.api.game.ManhuntEndReason;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import de.jozelot.jmanhunt.player.ManhuntPlayerImpl;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class MassManager {

    private final JManhunt plugin;
    private ManhuntStorage storage;

    public MassManager(JManhunt plugin) {
        this.plugin = plugin;
    }

    public boolean load() {
        String storageType = plugin.getBootstrap().getConfigManager().getStorageMethod();
        if (storageType.equalsIgnoreCase("SQLITE")) {
            storage = new SQLiteStorage(plugin);
        } else if (storageType.equalsIgnoreCase("MYSQL")) {
            storage = new MySQLStorage(plugin);
        } else {
            plugin.getLogger().log(Level.SEVERE, "");
            plugin.getLogger().log(Level.SEVERE, "Wrong storage method set!");
            plugin.getLogger().log(Level.SEVERE, "There is no: " + storageType);
            plugin.getLogger().log(Level.SEVERE, "");
            return false;
        }
        plugin.getLogger().log(Level.INFO, "Storage method is: " + storageType);

        storage.init();

        return true;
    }

    public GameState loadState() {
        String sql = "SELECT state FROM `" + storage.getPrefix() + "game` WHERE id = 1";

        try (Connection con = storage.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return GameState.valueOf(rs.getString("state"));
            }
        } catch (SQLException | IllegalArgumentException e) {
            return GameState.SETUP;
        }
        return GameState.SETUP;
    }

    public void saveState(GameState state) {
        String sql = "REPLACE INTO `" + storage.getPrefix() + "game` (id, state) VALUES (1, ?)";

        try (Connection con = storage.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, state.name());
            ps.executeUpdate();

        } catch (SQLException e) {
            plugin.getLogger().severe("Couldn't save game state: " + e.getMessage());
        }
    }

    public ManhuntEndReason loadEndReason() {
        String sql = "SELECT end_reason FROM `" + storage.getPrefix() + "game` WHERE id = 1";

        try (Connection con = storage.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String reason = rs.getString("end_reason");
                return reason != null ? ManhuntEndReason.valueOf(reason) : ManhuntEndReason.NONE;
            }
        } catch (SQLException | IllegalArgumentException e) {
            return ManhuntEndReason.NONE;
        }
        return ManhuntEndReason.NONE;
    }

    public void saveEndReason(ManhuntEndReason reason) {
        String sql = "UPDATE `" + storage.getPrefix() + "game` SET end_reason = ? WHERE id = 1";

        try (Connection con = storage.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, reason.name());
            ps.executeUpdate();

        } catch (SQLException e) {
            plugin.getLogger().severe("Couldn't save end reason: " + e.getMessage());
        }
    }

    public void saveManhuntPlayer(ManhuntPlayerImpl player) {
        String sql = "REPLACE INTO `" + storage.getPrefix() + "player` " +
                "(uuid, player_name, kills, deaths, lives, alive, team) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = storage.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, player.getUniqueId().toString());

            String nameToSave = "Unknown";

            if (player.getPlayer() != null) {
                nameToSave = player.getPlayer().getName();
            } else if (player.getLastKnownName() != null && !player.getLastKnownName().equalsIgnoreCase("Unknown")) {
                nameToSave = player.getLastKnownName();
            } else {
                String offlineName = Bukkit.getOfflinePlayer(player.getUniqueId()).getName();
                if (offlineName != null) {
                    nameToSave = offlineName;
                }
            }

            ps.setString(2, nameToSave);
            ps.setInt(3, player.getKills());
            ps.setInt(4, player.getDeaths());
            ps.setInt(5, player.getLives());
            ps.setBoolean(6, !player.isEliminated());
            ps.setString(7, player.getTeam().name());

            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't save " + player.getUniqueId() + ": ", e);
        }
    }

    public void loadManhuntPlayer(ManhuntPlayerImpl player) {
        String sql = "SELECT player_name, lives, alive, team, kills, deaths FROM `" + storage.getPrefix() + "player` WHERE uuid = ?";

        try (Connection con = storage.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    player.setLives(rs.getInt("lives"));
                    player.setKills(rs.getInt("kills"));
                    player.setDeaths(rs.getInt("deaths"));
                    player.setName(rs.getString("player_name"));

                    boolean wasAlive = rs.getBoolean("alive");
                    if (wasAlive) player.revive(); else player.eliminate();

                    try {
                        player.setTeamIntern(ManhuntTeam.valueOf(rs.getString("team")));
                    } catch (IllegalArgumentException e) {
                        player.setTeamIntern(ManhuntTeam.NONE);
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Konnte Spieler " + player.getUniqueId() + " nicht laden", e);
        }
    }

    public void saveAllPlayers(Collection<ManhuntPlayerImpl> players) {
        if (players.isEmpty()) return;

        plugin.getLogger().info("Saving " + players.size() + " player to the database...");
        for (ManhuntPlayerImpl player : players) {
            saveManhuntPlayer(player);
        }
    }

    public void clearAllData() {
        String[] tables = {"game", "player"};

        try (Connection con = storage.getConnection()) {
            con.setAutoCommit(false);

            for (String table : tables) {
                String sql = "DELETE FROM `" + storage.getPrefix() + table + "`";
                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.executeUpdate();
                }
            }

            String initSql = "REPLACE INTO `" + storage.getPrefix() + "game` (id, state, end_reason) VALUES (1, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(initSql)) {
                ps.setString(1, GameState.SETUP.name());
                ps.setString(2, ManhuntEndReason.NONE.name());
                ps.executeUpdate();
            }

            con.commit();
            plugin.getLogger().info("All manhunt data has been cleared from the database.");

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to clear database tables!", e);
        }
    }

    public List<ManhuntPlayerImpl> loadPlayersWithTeams() {
        List<ManhuntPlayerImpl> teamMembers = new ArrayList<>();
        String sql = "SELECT uuid, player_name, lives, alive, team, kills, deaths FROM `" + storage.getPrefix() + "player` WHERE team != 'NONE'";

        try (Connection con = storage.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String name = rs.getString("player_name");
                ManhuntPlayerImpl player = new ManhuntPlayerImpl(uuid, name, plugin);

                player.setLives(rs.getInt("lives"));
                player.setKills(rs.getInt("kills"));
                player.setDeaths(rs.getInt("deaths"));

                if (rs.getBoolean("alive")) player.revive(); else player.eliminate();

                try {
                    player.setTeamIntern(ManhuntTeam.valueOf(rs.getString("team")));
                } catch (IllegalArgumentException e) {
                    player.setTeamIntern(ManhuntTeam.NONE);
                }

                teamMembers.add(player);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load players with teams: ", e);
        }
        return teamMembers;
    }

    public UUID getUUIDByName(String name) {
        String sql = "SELECT uuid FROM `" + storage.getPrefix() + "player` WHERE player_name = ? LIMIT 1";

        try (Connection con = storage.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return UUID.fromString(rs.getString("uuid"));
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            return null;
        }
        return null;
    }

    public ManhuntStorage getStorage() {
        return storage;
    }
}
