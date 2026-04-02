/*
 * Copyright (c) 2026 jozelot. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api.event;

import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a player's team affiliation is about to change.
 * <p>
 * This typically occurs during the setup or pre-game phases. If canceled,
 * the player will remain in their current team.
 * </p>
 *
 * @author jozelot_
 * @since 1.0.0
 */
public class ManhuntTeamAssignEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ManhuntPlayer player;
    private final ManhuntTeam oldTeam;
    private ManhuntTeam newTeam;
    private boolean cancelled;

    /**
     * Constructs a new ManhuntTeamAssignEvent.
     *
     * @param player  the player whose team is changing
     * @param oldTeam the team the player is leaving
     * @param newTeam the team the player is joining
     */
    public ManhuntTeamAssignEvent(@NotNull ManhuntPlayer player, @NotNull ManhuntTeam oldTeam, @NotNull ManhuntTeam newTeam) {
        this.player = player;
        this.oldTeam = oldTeam;
        this.newTeam = newTeam;
    }

    /**
     * Gets the player involved in the team assignment.
     *
     * @return the manhunt player
     */
    public @NotNull ManhuntPlayer getPlayer() {
        return player;
    }

    /**
     * Gets the team the player belonged to before this event.
     *
     * @return the previous {@link ManhuntTeam}
     */
    public @NotNull ManhuntTeam getOldTeam() {
        return oldTeam;
    }

    /**
     * Gets the team that will be assigned to the player if the event is not canceled.
     *
     * @return the proposed {@link ManhuntTeam}
     */
    public @NotNull ManhuntTeam getNewTeam() {
        return newTeam;
    }

    /**
     * Overrides the team that will be assigned to the player.
     *
     * @param newTeam the new target team
     */
    public void setNewTeam(@NotNull ManhuntTeam newTeam) {
        this.newTeam = newTeam;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}