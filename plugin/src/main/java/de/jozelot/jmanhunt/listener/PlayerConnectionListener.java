package de.jozelot.jmanhunt.listener;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.event.AdminJoinEvent;
import de.jozelot.jmanhunt.api.event.DisallowReason;
import de.jozelot.jmanhunt.api.event.ManhuntLoginEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private final JManhunt plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public PlayerConnectionListener(JManhunt plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        ManhuntLoginEvent apiEvent = new ManhuntLoginEvent(
                player,
                DisallowReason.NONE,
                Component.empty()
        );

        apiEvent.setResult(ManhuntLoginEvent.Result.ALLOWED);

        if (plugin.getBootstrap().getPhaseManager().isSetup() && !player.hasPermission("jmanhunt.setup.bypass")) {
            apiEvent.setResult(ManhuntLoginEvent.Result.DENIED);
            apiEvent.setReason(DisallowReason.SETUP_MODE);
            apiEvent.setMessage(mm.deserialize(plugin.getBootstrap().getLangManager().format("phase-setup-player-kick", null)));
        }

        Bukkit.getPluginManager().callEvent(apiEvent);

        if (apiEvent.getResult() == ManhuntLoginEvent.Result.DENIED) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, apiEvent.getMessage());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("jmanhunt.admin")) {
            AdminJoinEvent adminJoinEvent = new AdminJoinEvent(player);
            Bukkit.getPluginManager().callEvent(adminJoinEvent);
        }

        plugin.getBootstrap().getManhuntPlayerManager().createPlayer(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        plugin.getBootstrap().getManhuntPlayerManager().removePlayer(player);
    }
}
