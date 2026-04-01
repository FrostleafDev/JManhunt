package de.jozelot.jmanhunt.api.event;

import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ManhuntTeamAssignEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final ManhuntPlayer player;
    private final ManhuntTeam oldTeam;
    private ManhuntTeam newTeam;
    private boolean cancelled;

    public ManhuntTeamAssignEvent(ManhuntPlayer player, ManhuntTeam oldTeam, ManhuntTeam newTeam) {
        this.player = player;
        this.oldTeam = oldTeam;
        this.newTeam = newTeam;
    }

    public ManhuntPlayer getPlayer() {
        return player;
    }

    public ManhuntTeam getOldTeam() {
        return oldTeam;
    }

    public ManhuntTeam getNewTeam() {
        return newTeam;
    }

    public void setNewTeam(ManhuntTeam newTeam) {
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

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
