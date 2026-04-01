package de.jozelot.jmanhunt.api.event;

import de.jozelot.jmanhunt.api.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AdminJoinEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;

    public AdminJoinEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
