/*
 * Copyright (c) 2026 jozelot. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api.event;

/**
 * This enum is for reasons why a given event
 * won't be allowed.
 *
 * @author jozelot_
 * @since 1.0.0
 */
public enum DisallowReason {

    /**
     * Server is in setup
     */
    SETUP_MODE,

    /**
     * The manhunt is already running
     */
    GAME_RUNNING,

    /**
     * This means that the server ist full.
     * And players can't join
     */
    SERVER_FULL,

    /**
     * This means there is a custom reason.
     * This can be used by addon developers
     */
    CUSTOM,

    /**
     * None is used when there is no specific reason
     * But the event is disallowed anyway
     */
    NONE;
}
