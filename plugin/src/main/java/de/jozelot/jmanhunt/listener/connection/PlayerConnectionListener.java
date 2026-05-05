package de.jozelot.jmanhunt.listener.connection;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.event.AdminJoinEvent;
import de.jozelot.jmanhunt.api.event.DisallowReason;
import de.jozelot.jmanhunt.api.event.ManhuntLoginEvent;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import de.jozelot.jmanhunt.api.player.Sound;
import de.jozelot.jmanhunt.player.ManhuntPlayerImpl;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

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

        if (plugin.getBootstrap().getPhaseManager().isSetup() && !player.hasPermission("jmanhunt.setup.bypass") && !plugin.getBootstrap().getConfigManager().canPlayersJoinDuringSetup()) {
            apiEvent.setResult(ManhuntLoginEvent.Result.DENIED);
            apiEvent.setReason(DisallowReason.SETUP_MODE);
            apiEvent.setMessage(mm.deserialize(String.join("<newline>", plugin.getBootstrap().getLangManager().formatList("phase-setup-player-kick", null))));
        }

        Bukkit.getPluginManager().callEvent(apiEvent);

        if (apiEvent.getResult() == ManhuntLoginEvent.Result.DENIED) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, apiEvent.getMessage());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ManhuntPlayerImpl manhuntPlayer = plugin.getBootstrap().getManhuntPlayerManager().getOrLoadPlayer(player.getUniqueId(), player.getName());
        manhuntPlayer.setOnline(true);
        manhuntPlayer.giveCompass();

        if (player.hasPermission("jmanhunt.admin")) {
            AdminJoinEvent adminJoinEvent = new AdminJoinEvent(player);
            Bukkit.getPluginManager().callEvent(adminJoinEvent);
        }

        if (plugin.getBootstrap().getConfigManager().sendCustomConnectionMessages()) {
            event.joinMessage(Component.empty());

            Component messageComp = mm.deserialize(plugin.getBootstrap().getLangManager().format("join-message", Map.of("player_name", event.getPlayer().getName())));
            Bukkit.getConsoleSender().sendMessage(messageComp);

            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target != player) target.sendMessage(messageComp);
            }
        }
        if (plugin.getBootstrap().getConfigManager().playJoinSound()) {
            manhuntPlayer.playSound(Sound.SUCCESS);
        }
        if (plugin.getBootstrap().getGameManager().getPhaseManager().isRunning()) {
            if (manhuntPlayer.getTeam() == ManhuntTeam.SPECTATOR || manhuntPlayer.isEliminated()) player.setGameMode(GameMode.SPECTATOR);
        }
        if (plugin.getBootstrap().getConfigManager().getTablist().isEnabled()) {
            plugin.getBootstrap().getCustomTablist().applyTablist(manhuntPlayer);
        }
        if (plugin.getBootstrap().getConfigManager().getTeamPrefix().isTab()) {
            plugin.getBootstrap().getCustomTablist().updateTabName(manhuntPlayer);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        plugin.getBootstrap().getManhuntPlayerManager().removePlayer(player);

        if (plugin.getBootstrap().getConfigManager().sendCustomConnectionMessages()) {
            event.quitMessage(Component.empty());

            Component messageComp = mm.deserialize(plugin.getBootstrap().getLangManager().format("leave-message", Map.of("player_name", event.getPlayer().getName())));
            Bukkit.getConsoleSender().sendMessage(messageComp);

            for (Player target : Bukkit.getOnlinePlayers()) {
                if (target != player) target.sendMessage(messageComp);
            }
        }
    }
}
