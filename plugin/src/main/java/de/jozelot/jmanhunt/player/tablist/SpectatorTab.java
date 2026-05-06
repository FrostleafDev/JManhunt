package de.jozelot.jmanhunt.player.tablist;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
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

        List<Player> participants = playerManager.getActiveParticipants().stream()
                .filter(ManhuntPlayer::isOnline)
                .map(ManhuntPlayer::getPlayer)
                .toList();

        List<Player> spectators = playerManager.getSpectators().stream()
                .filter(ManhuntPlayer::isOnline)
                .map(ManhuntPlayer::getPlayer)
                .toList();

        for (Player p : participants) {
            if (!visibility && p.hasPermission("jmanhunt.see.spectators")) continue;

            for (Player target : spectators) {
                if (p.equals(target)) continue;

                if (!visibility) {
                    p.hidePlayer(plugin, target);
                } else {
                    p.showPlayer(plugin, target);
                }
            }
        }
    }
}
