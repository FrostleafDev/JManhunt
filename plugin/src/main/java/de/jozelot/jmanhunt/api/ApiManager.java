package de.jozelot.jmanhunt.api;

import de.jozelot.jmanhunt.JManhunt;

import java.util.logging.Level;

public class ApiManager {

    private final JManhunt plugin;
    private ManhuntImpl manhunt;

    public ApiManager(JManhunt plugin) {
        this.plugin = plugin;
    }

    public boolean setup() {
        try {
            this.manhunt = new ManhuntImpl(plugin);
            ManhuntProvider.register(manhunt);
            return true;

        } catch (IllegalStateException e) {
            plugin.getLogger().log(Level.SEVERE, "");
            plugin.getLogger().log(Level.SEVERE, "API already registered: " + e.getMessage());
            plugin.getLogger().log(Level.SEVERE, "");
            return false;
        }
    }

    public void shutdown() {
        ManhuntProvider.unregister();
    }

    public ManhuntImpl getManhunt() {
        return manhunt;
    }
}
