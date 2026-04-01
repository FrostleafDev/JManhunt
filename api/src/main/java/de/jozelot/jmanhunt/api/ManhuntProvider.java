package de.jozelot.jmanhunt.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class ManhuntProvider {

    private static Manhunt instance = null;

    private ManhuntProvider() {
        throw new UnsupportedOperationException("This is a provider class and cannot be instantiated");
    }

    /**
     * @return the current API instance
     */
    public static @NotNull Manhunt get() {
        if (instance == null) {
            throw new IllegalStateException("Manhunt API is not initialized yet!");
        }
        return instance;
    }

    /**
     * This method gets called by the core plugin
     */
    @ApiStatus.Internal
    public static void register(@NotNull Manhunt manhuntInstance) {
        if (instance != null) {
            throw new IllegalStateException("Manhunt API is already registered!");
        }
        instance = manhuntInstance;
    }

    @ApiStatus.Internal
    public static void unregister() {
        instance = null;
    }
}
