package de.jozelot.jmanhunt.listener;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.event.AdminJoinEvent;
import de.jozelot.jmanhunt.api.player.Sound;
import de.jozelot.jmanhunt.player.ManhuntPlayerImpl;
import de.jozelot.jmanhunt.utility.PlaySoundUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class AdminJoinListener implements Listener {

    private final JManhunt plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public AdminJoinListener(JManhunt plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAdminJoin(AdminJoinEvent event) {
        if (plugin.getBootstrap().getPhaseManager().isSetup()) {
            event.getPlayer().sendMessage(mm.deserialize(String.join("<newline>", plugin.getBootstrap().getLangManager().formatList("admin-setup-join-info", null))));
            PlaySoundUtils.playPling(event.getPlayer(), plugin);
        }
        if (!plugin.getBootstrap().getUpdateManager().isUpdateAvailable()) {
            return;
        }

        Player player = event.getPlayer();
        ManhuntPlayerImpl manhuntPlayer = (ManhuntPlayerImpl) plugin.getBootstrap().getManhuntPlayerManager().getPlayer(player);
        String latestVersion = plugin.getBootstrap().getUpdateManager().getLatestVersion();

        player.sendMessage(mm.deserialize(String.join("<newline>", plugin.getBootstrap().getLangManager().formatList("admin-update-info", Map.of("version", latestVersion)))));
        manhuntPlayer.playSound(Sound.NOTIFY);
    }
}
