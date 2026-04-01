package de.jozelot.jmanhunt.api.player;

import org.bukkit.Warning;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface ManhuntPlayer {

    /**
     * Get the current team of the player
     * @return {@link ManhuntTeam}
     */
    ManhuntTeam getTeam();

    /**
     * Forces a team change for this player.
     * * @warning Calling this during an active game will bypass game logic (e.g. win conditions,
     * spawn points, and item distribution). Use the GameSession's join methods
     * for safe team assignments instead.
     * @param team The new team to assign.
     */
    @Warning(reason = "Bypasses internal game flow logic and win condition checks.")
    void setTeam(ManhuntTeam team);

    UUID getUniqueId();
    Player getPlayer();

    /**
     * @return true if the player has lost all their lives and is out of the race.
     * For non-runners, this typically returns false.
     */
    boolean isEliminated();

    /**
     * Removes the player from active gameplay.
     * If called on a runner, they lose their active status.
     * If called on others, behavior depends on implementation (e.g., kick or spectator).
     */
    void eliminate();

    /**
     * Returns the player to active gameplay if they were eliminated.
     */
    void revive();

    /**
     * @return The number of lives. Non-runners return 0 as they don't participate in the life system.
     */
    int getLives();

    /**
     * Note: If the runner is that this will revive him.
     * @param lives The amount of lives the runner will have. Non-runners won't be affected that method
     */
    void setLives(int lives);

    int getKills();
    int addKill();
    int removeKill();
    void setKills(int kills);

    int getDeaths();
    int addDeath();
    int removeDeath();
    void setDeaths(int kills);
}
