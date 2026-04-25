package de.jozelot.jmanhunt.registry;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.listener.*;

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
        pm.registerEvents(new AdminJoinListener(plugin), plugin);
        pm.registerEvents(new ManhuntItemListener(plugin), plugin);
        pm.registerEvents(new ServerPingListener(plugin), plugin);
        pm.registerEvents(new SetupPreGameProtection(plugin), plugin);
        pm.registerEvents(new PauseGameFreezeListener(plugin), plugin);
    }
}
