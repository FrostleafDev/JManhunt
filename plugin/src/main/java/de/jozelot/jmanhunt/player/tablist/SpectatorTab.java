package de.jozelot.jmanhunt.player.tablist;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import org.bukkit.entity.Player;

import java.util.List;

public class SpectatorTab {

    private final JManhunt plugin;

    public SpectatorTab(JManhunt plugin) {
        this.plugin = plugin;
    }

    public void updateSpectators() {
        boolean visibility = plugin.getBootstrap().getConfigManager().isSpectatorVisibility();
        var playerManager = plugin.getBootstrap().getManhuntPlayerManager();

        List<Player> allPlayers = plugin.getBootstrap().getManhuntPlayerManager().getPlayers().stream()
                .filter(ManhuntPlayer::isOnline)
                .map(ManhuntPlayer::getPlayer)
                .toList();

        List<Player> spectators = playerManager.getSpectators().stream()
                .filter(ManhuntPlayer::isOnline)
                .map(ManhuntPlayer::getPlayer)
                .toList();

        for (Player viewer : allPlayers) {
            if (visibility) {
                for (Player target : spectators) {
                    if (!viewer.equals(target)) viewer.showPlayer(plugin, target);
                }
                continue;
            }

            if (viewer.hasPermission("jmanhunt.see.spectators")) {
                for (Player target : spectators) {
                    if (!viewer.equals(target)) viewer.showPlayer(plugin, target);
                }
                continue;
            }

            ManhuntPlayer viewerMP = playerManager.getPlayer(viewer.getUniqueId());
            if (viewerMP != null && viewerMP.getTeam() == ManhuntTeam.SPECTATOR) {
                for (Player target : spectators) {
                    if (!viewer.equals(target)) viewer.showPlayer(plugin, target);
                }
                continue;
            }

            for (Player target : spectators) {
                viewer.hidePlayer(plugin, target);
            }
        }
    }
}
