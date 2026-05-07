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

        for (Player viewer : allPlayers) {
            ManhuntPlayer viewerMP = playerManager.getPlayer(viewer.getUniqueId());

            for (Player target : allPlayers) {
                if (viewer.equals(target)) continue;

                ManhuntPlayer targetMP = playerManager.getPlayer(target.getUniqueId());
                if (targetMP == null) continue;

                if (targetMP.getTeam() == ManhuntTeam.SPECTATOR) {
                    if (visibility || viewer.hasPermission("jmanhunt.see.spectators") ||
                            (viewerMP != null && viewerMP.getTeam() == ManhuntTeam.SPECTATOR)) {
                        viewer.showPlayer(plugin, target);
                    } else {
                        viewer.hidePlayer(plugin, target);
                    }
                } else {
                    viewer.showPlayer(plugin, target);
                }
            }
        }
    }
}
