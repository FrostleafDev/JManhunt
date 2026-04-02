/*
 * Copyright (c) 2026 jozelot. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * The central provider class for the JManhunt API.
 * <p>
 * This class allows other plugins to access the API instance and its managers.
 * It follows the provider pattern to ensure a singleton-like access point.
 * </p>
 *
 * @author jozelot_
 * @since 1.0.0
 */
public final class ManhuntProvider {

    /**
     * The internal singleton instance of the API.
     */
    private static Manhunt instance = null;

    private ManhuntProvider() {
        throw new UnsupportedOperationException("This is a provider class and cannot be instantiated");
    }

    /**
     * Gets the current instance of the JManhunt API.
     *
     * @return the active API instance
     * @throws IllegalStateException if the API has not been initialized yet
     */
    public static @NotNull Manhunt get() {
        if (instance == null) {
            throw new IllegalStateException("Manhunt API is not initialized yet!");
        }
        return instance;
    }

    /**
     * Registers a new API instance.
     * <p>
     * This method is intended for internal use by the core plugin only.
     * </p>
     *
     * @param manhuntInstance the instance to register
     * @throws IllegalStateException if an API instance is already registered
     */
    @ApiStatus.Internal
    public static void register(@NotNull Manhunt manhuntInstance) {
        if (instance != null) {
            throw new IllegalStateException("Manhunt API is already registered!");
        }
        instance = manhuntInstance;
    }

    /**
     * Unregisters the current API instance.
     * <p>
     * This method is intended for internal use by the core plugin only.
     * </p>
     */
    @ApiStatus.Internal
    public static void unregister() {
        instance = null;
    }
}
