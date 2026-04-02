package de.jozelot.jmanhunt.player;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntPlayerManager;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
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
    @Deprecated(since = "1.0.0", forRemoval = true)
    public ManhuntPlayerImpl createPlayer(UUID uuid) {
        ManhuntPlayerImpl player = new ManhuntPlayerImpl(uuid, plugin);
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
    @Deprecated(since = "1.0.0", forRemoval = true)
    public ManhuntPlayerImpl createPlayer(Player player) {
        return createPlayer(player.getUniqueId());
    }

    /**
     * Removes a player from the server and saves it data to the database
     * This is only called when the player leaves the server
     * @param uuid
     */
    public void removePlayer(UUID uuid) {
        ManhuntPlayerImpl player = players.get(uuid);

        if (player != null) {
            player.setOnline(false);
            String playerName = Bukkit.getOfflinePlayer(uuid).getName();

            Runnable saveTask = () -> plugin.getBootstrap().getMassManager().saveManhuntPlayer(player);
            if (!plugin.isEnabled()) {
                saveTask.run();
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, saveTask);
            }

            if (player.getTeam() == ManhuntTeam.NONE) {
                players.remove(uuid);
                plugin.getLogger().info("Saved and removed idle player: " + playerName);
            } else {
                plugin.getLogger().info("Saved and kept team member in RAM (offline): " + playerName);
            }
        }
    }

    public ManhuntPlayerImpl getOrLoadPlayer(UUID uuid, String name) {
        if (players.containsKey(uuid)) {
            ManhuntPlayerImpl existing = players.get(uuid);
            existing.setName(name);
            return existing;
        }

        ManhuntPlayerImpl player = new ManhuntPlayerImpl(uuid, name, plugin);
        plugin.getBootstrap().getMassManager().loadManhuntPlayer(player);
        players.put(uuid, player);
        return player;
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
            getOrLoadPlayer(player.getUniqueId(), player.getName()).setOnline(true);
        }

        List<ManhuntPlayerImpl> teamMembers = plugin.getBootstrap().getMassManager().loadPlayersWithTeams();
        for (ManhuntPlayerImpl member : teamMembers) {
            players.putIfAbsent(member.getUniqueId(), member);
        }
    }

    public void removePlayer(Player player) {
        removePlayer(player.getUniqueId());
    }

    public void removePlayer(ManhuntPlayerImpl player) {
        removePlayer(player.getUniqueId());
    }

    @Override
    @Nullable
    public ManhuntPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    @Override
    @Nullable
    public ManhuntPlayer getPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    @Override
    public void getPlayerByName(String name, Consumer<ManhuntPlayer> callback) {
        for (ManhuntPlayerImpl player : players.values()) {
            if (player.getLastKnownName().equalsIgnoreCase(name)) {
                callback.accept(player);
                return;
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            UUID uuid = plugin.getBootstrap().getMassManager().getUUIDByName(name);

            if (uuid == null) {
                Bukkit.getScheduler().runTask(plugin, () -> callback.accept(null));
                return;
            }

            final UUID finalUuid = uuid;
            Bukkit.getScheduler().runTask(plugin, () -> {
                ManhuntPlayerImpl player = getOrLoadPlayer(finalUuid, name);
                callback.accept(player);
            });
        });
    }

    public void getOrCreatePlayerByName(String name, Consumer<ManhuntPlayerImpl> callback) {
        for (ManhuntPlayerImpl player : players.values()) {
            if (player.getLastKnownName().equalsIgnoreCase(name)) {
                callback.accept(player);
                return;
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            UUID uuid = plugin.getBootstrap().getMassManager().getUUIDByName(name);

            if (uuid == null) {
                uuid = getUuidFromMojang(name);

                if (uuid == null) {
                    Bukkit.getScheduler().runTask(plugin, () -> callback.accept(null));
                    return;
                }
            }

            final UUID finalUuid = uuid;
            Bukkit.getScheduler().runTask(plugin, () -> {
                ManhuntPlayerImpl player = getOrLoadPlayer(finalUuid, name);
                callback.accept(player);
            });
        });
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

    @Nullable
    private UUID getUuidFromMojang(String playerName) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);

            if (connection.getResponseCode() == 200) {
                try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    String uuidString = json.get("id").getAsString();
                    return UUID.fromString(uuidString.replaceFirst(
                            "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{12})",
                            "$1-$2-$3-$4-$5"
                    ));
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
