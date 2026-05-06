package de.jozelot.jmanhunt.core;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import org.bukkit.scheduler.BukkitTask;

public class Heartbeat {

    private final JManhunt plugin;
    private BukkitTask task;
    private int ticks = 0;

    public Heartbeat(JManhunt plugin) {
        this.plugin = plugin;
    }

    public void startHeartbeat() {
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {

            // 1 Second Clock
            if (ticks % 20 == 0) {
                if (plugin.getBootstrap().getConfigManager().getTablist().isEnabled()) {
                    plugin.getBootstrap().getManhuntPlayerManager().getPlayers().stream().filter(ManhuntPlayer::isOnline).forEach(p -> {
                        plugin.getBootstrap().getCustomTablist().applyTablist(p);
                    });
                }
                if (plugin.getBootstrap().getConfigManager().getTeamPrefix().isTab()) {
                    plugin.getBootstrap().getManhuntPlayerManager().getPlayers().stream().filter(ManhuntPlayer::isOnline).forEach(p -> {
                        plugin.getBootstrap().getCustomTablist().updateTabName(p);
                    });
                }
                if (plugin.getBootstrap().getConfigManager().getTeamPrefix().isNametags()) {
                    plugin.getBootstrap().getManhuntPlayerManager().getPlayers().stream().filter(ManhuntPlayer::isOnline).forEach(p -> {
                        plugin.getBootstrap().getPlayerNameTags().updateNameTag(p);
                    });
                }
            }
            // CLEAN UP
            if (ticks >= 72000) ticks = 0;

            ticks++;
        },0L, 1L);
    }

    public void stopHeartbeat() {
        if (task != null) {
            task.cancel();
            task = null;
            ticks = 0;
        }
    }
}
