/*
 * Copyright (c) 2026 jozelot. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a player with administrative privileges joins the server.
 * <p>
 * This event can be used to notify other staff members or to apply
 * specific administrative settings upon login.
 * </p>
 *
 * @author jozelot_
 * @since 1.0.0
 */
public class AdminJoinEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;

    /**
     * Constructs a new AdminJoinEvent.
     *
     * @param player the admin who joined
     */
    public AdminJoinEvent(@NotNull Player player) {
        this.player = player;
    }

    /**
     * Gets the player who triggered this event.
     *
     * @return the administrative player
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}