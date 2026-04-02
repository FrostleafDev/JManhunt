/*
 * Copyright (c) 2026 jozelot. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api.player;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Manager for handling and retrieving {@link ManhuntPlayer} instances.
 *
 * @author jozelot_
 * @since 1.0.0
 */
public interface ManhuntPlayerManager {

    /**
     * Gets a {@link ManhuntPlayer} by their unique ID.
     *
     * @param uuid the UUID of the player to retrieve
     * @return the manhunt player, or {@code null} if no player is registered with this UUID
     */
    @Nullable
    ManhuntPlayer getPlayer(UUID uuid);

    /**
     * Gets a {@link ManhuntPlayer} from a Bukkit {@link Player} instance.
     *
     * @param player the Bukkit player instance
     * @return the manhunt player, or {@code null} if the player is not currently tracked
     */
    @Nullable
    ManhuntPlayer getPlayer(Player player);

    /**
     * Gets an unmodifiable collection of all currently tracked players.
     *
     * @return a collection of all registered {@link ManhuntPlayer}s
     */
    Collection<ManhuntPlayer> getPlayers();

    /**
     * Gets all players who are actively participating in the manhunt (Runners and Hunters).
     *
     * @return a collection of all active participants
     */
    Collection<ManhuntPlayer> getActiveParticipants();

    /**
     * Gets all players currently assigned to the runner team.
     *
     * @return a collection of all runners
     */
    Collection<ManhuntPlayer> getRunners();

    /**
     * Gets all players currently assigned to the hunter team.
     *
     * @return a collection of all hunters
     */
    Collection<ManhuntPlayer> getHunters();

    /**
     * Gets all players currently assigned to the spectator team.
     *
     * @return a collection of all spectators
     */
    Collection<ManhuntPlayer> getSpectators();

    /**
     * Gets all players who have not yet been assigned to a specific team.
     * <p>
     * Note: This usually only returns players during the pre-game or setup phase,
     * as unassigned players are typically moved to the spectator team once the game starts.
     * </p>
     *
     * @return a collection of players without a team
     */
    Collection<ManhuntPlayer> getPlayersWithoutTeam();
}
