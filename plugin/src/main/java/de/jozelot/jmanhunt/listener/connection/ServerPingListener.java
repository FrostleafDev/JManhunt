package de.jozelot.jmanhunt.listener.connection;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.storage.ConfigManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
        var serverList = config.getServerList();

        if (serverList.getMotd().isEnabled()) {
            event.motd(mm.deserialize(String.join("<newline>", serverList.getMotd().getLines())));
        }

        if (serverList.getPlayerCount().isEnabled()) {
            switch (serverList.getPlayerCount().getMode()) {
                case STATIC -> event.setMaxPlayers(serverList.getPlayerCount().getValue());
                case DYNAMIC -> event.setMaxPlayers(plugin.getServer().getOnlinePlayers().size() + serverList.getPlayerCount().getValue());
            }
        }
    }
}

