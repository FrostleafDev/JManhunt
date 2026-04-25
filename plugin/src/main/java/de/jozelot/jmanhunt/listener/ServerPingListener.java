package de.jozelot.jmanhunt.listener;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.storage.ConfigManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerPingListener implements Listener {

    private final JManhunt plugin;
    private final ConfigManager config;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ServerPingListener(JManhunt plugin) {
        this.plugin = plugin;
        this.config = plugin.getBootstrap().getConfigManager();
    }

    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
        if (config.getMotd().isEnabled()) {
            event.motd(mm.deserialize(String.join("<newline>", config.getMotd().getLines())));
        }
    }
}

