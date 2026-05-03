package de.jozelot.jmanhunt.registry;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.listener.ChatListener;
import de.jozelot.jmanhunt.listener.connection.AdminJoinListener;
import de.jozelot.jmanhunt.listener.connection.PlayerConnectionListener;
import de.jozelot.jmanhunt.listener.connection.ServerPingListener;
import de.jozelot.jmanhunt.listener.mechanic.ManhuntItemListener;
import de.jozelot.jmanhunt.listener.mechanic.MenuClickListener;
import de.jozelot.jmanhunt.listener.mechanic.PlayerRespawnListener;
import de.jozelot.jmanhunt.listener.mechanic.StartProtection;
import de.jozelot.jmanhunt.listener.protection.PauseGameFreezeListener;
import de.jozelot.jmanhunt.listener.protection.SetupPreGameProtection;

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
        pm.registerEvents(new PlayerRespawnListener(plugin), plugin);
        pm.registerEvents(new MenuClickListener(plugin), plugin);
        pm.registerEvents(new ChatListener(plugin), plugin);
        pm.registerEvents(new StartProtection(plugin), plugin);
    }
}
