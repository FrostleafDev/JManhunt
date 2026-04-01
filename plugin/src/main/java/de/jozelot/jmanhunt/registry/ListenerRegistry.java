package de.jozelot.jmanhunt.registry;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.listener.PlayerConnectionListener;

public class ListenerRegistry {

    private final JManhunt plugin;

    public ListenerRegistry(JManhunt plugin) {
        this.plugin = plugin;
    }

    /**
     * All Bukkit Listeners are registered here
     */
    public void register() {
        var pm = plugin.getServer().getPluginManager();

        pm.registerEvents(new PlayerConnectionListener(plugin), plugin);
    }
}
