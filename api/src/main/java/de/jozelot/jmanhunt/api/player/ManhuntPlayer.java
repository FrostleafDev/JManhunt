/*
 * Copyright (c) 2026 jozelot. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api.player;

import org.bukkit.Warning;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a player within the Manhunt game context.
 * This object manages the player's state, statistics, and team affiliation.
 *
 * @author jozelot_
 * @since 1.0.0
 */
public interface ManhuntPlayer {

    /**
     * Gets the current team of the player.
     *
     * @return the assigned {@link ManhuntTeam}
     */
    @NotNull
    ManhuntTeam getTeam();

    /**
     * Forces a team change for this player.
     * <p>
     * <b>Warning:</b> Calling this during an active game will bypass game logic (e.g., win conditions,
     * spawn points, and item distribution).
     * </p>
     *
     * @param team the new team to assign
     */
    @Warning(reason = "Bypasses internal game flow logic and win condition checks.")
    void setTeam(ManhuntTeam team);

    /**
     * @return the unique ID of the player
     */
    @NotNull
    UUID getUniqueId();

    /**
     * @return the underlying Bukkit {@link Player} instance
     */
    @NotNull
    Player getPlayer();

    /**
     * Checks if the player is currently eliminated from the game.
     *
     * @return true if the player has lost all lives; false otherwise
     */
    boolean isEliminated();

    /**
     * Removes the player from active gameplay and marks them as eliminated.
     */
    void eliminate();

    /**
     * Returns the player to active gameplay and restores their ability to participate.
     */
    void revive();

    /**
     * Gets the remaining lives of the player.
     *
     * @return the number of lives; typically 0 for non-runners
     */
    int getLives();

    /**
     * Sets the player's life count. Setting this to a value greater than 0
     * will typically revive an eliminated runner.
     *
     * @param lives the new amount of lives
     */
    void setLives(int lives);

    /**
     * @return the total number of kills achieved by this player
     */
    int getKills();

    /**
     * Increments the player's kill count by one.
     *
     * @return the new total kill count
     */
    int addKill();

    /**
     * Decrements the player's kill count by one.
     *
     * @return the new total kill count
     */
    int removeKill();

    /**
     * @param kills the new total kill count
     */
    void setKills(int kills);

    /**
     * @return the total number of deaths of this player
     */
    int getDeaths();

    /**
     * Increments the player's death count by one.
     *
     * @return the new total death count
     */
    int addDeath();

    /**
     * Decrements the player's death count by one.
     *
     * @return the new total death count
     */
    int removeDeath();

    /**
     * @param deaths the new total death count
     */
    void setDeaths(int deaths);

    /**
     * Plays an ui sound for the player
     * @param sound The sound. These are the sounds from the config
     */
    void playSound(@NotNull Sound sound);
}
