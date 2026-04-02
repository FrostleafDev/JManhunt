/*
 * Copyright (c) 2026 jozelot. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api.event;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a player attempts to participate in a manhunt session.
 * <p>
 * This event is used by the core plugin to determine if a player is allowed to join
 * based on the current game state, permissions, or server capacity.
 * </p>
 *
 * @author jozelot_
 * @since 1.0.0
 */
public class ManhuntLoginEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private DisallowReason reason;
    private Result result;
    private Component message;

    /**
     * Constructs a new ManhuntLoginEvent.
     *
     * @param player  the player attempting to join
     * @param reason  the initial reason for disallowing the join
     * @param message the message shown to the player if denied
     */
    public ManhuntLoginEvent(@NotNull Player player, @NotNull DisallowReason reason, @NotNull Component message) {
        this.player = player;
        this.reason = reason;
        this.result = Result.DENIED;
        this.message = message;
    }

    /**
     * Represents the outcome of the login attempt.
     */
    public enum Result {
        /**
         * The player is permitted to join.
         */
        ALLOWED,
        /**
         * The player is prevented from joining.
         */
        DENIED
    }

    /**
     * @return the current disallow reason
     */
    public @NotNull DisallowReason getReason() {
        return reason;
    }

    /**
     * @return the result of the login attempt
     */
    public @NotNull Result getResult() {
        return result;
    }

    /**
     * @return the player who is attempting to join
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * @return the kick or denial message
     */
    public @NotNull Component getMessage() {
        return message;
    }

    /**
     * @param reason the new reason for disallowing the join
     */
    public void setReason(@NotNull DisallowReason reason) {
        this.reason = reason;
    }

    /**
     * @param result the new result of the login attempt
     */
    public void setResult(@NotNull Result result) {
        this.result = result;
    }

    /**
     * @param message the new message to be displayed upon denial
     */
    public void setMessage(@NotNull Component message) {
        this.message = message;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Required for Bukkit's event system.
     *
     * @return the handler list
     */
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}