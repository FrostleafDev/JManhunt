package de.jozelot.jmanhunt.listener;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.game.PhaseManagerImpl;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.swing.plaf.PanelUI;

public class PauseGameFreezeListener implements Listener {

    private final JManhunt plugin;
    private final PhaseManagerImpl phaseManager;

    public PauseGameFreezeListener(JManhunt plugin) {
        this.plugin = plugin;
        this.phaseManager = plugin.getBootstrap().getPhaseManager();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!phaseManager.isPaused()) return;
        if (!plugin.getBootstrap().getConfigManager().isPauseFreezePlayer()) return;
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        var from = event.getFrom();
        var to = event.getTo();

        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
            return;
        }

        var freezeLocation = from.clone();
        freezeLocation.setYaw(to.getYaw());
        freezeLocation.setPitch(to.getPitch());

        event.setTo(freezeLocation);
    }
}
