package de.jozelot.jmanhunt.listener;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.event.AdminJoinEvent;
import de.jozelot.jmanhunt.api.player.Sound;
import de.jozelot.jmanhunt.player.ManhuntPlayerImpl;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AdminJoinListener implements Listener {

    private final JManhunt plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public AdminJoinListener(JManhunt plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAdminJoin(AdminJoinEvent event) {
        if (!plugin.getBootstrap().getUpdateManager().isUpdateAvailable()) {
            return;
        }

        Player player = event.getPlayer();
        ManhuntPlayerImpl manhuntPlayer = (ManhuntPlayerImpl) plugin.getBootstrap().getManhuntPlayerManager().getPlayer(player);

        player.sendMessage(mm.deserialize(String.join("<newline>", plugin.getBootstrap().getLangManager().getUpdateInfo())));
        manhuntPlayer.playSound(Sound.NOTIFY);
    }
}
