package de.jozelot.jmanhunt.listener.custom;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.event.ManhuntTeamAssignEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public class TeamUpdateListener implements Listener {

    private final JManhunt plugin;

    public TeamUpdateListener(JManhunt plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTeamUpdate(ManhuntTeamAssignEvent event) {
        plugin.getLogger().log(Level.WARNING, "Event wurde gecalled");
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getBootstrap().getSpectatorTab().updateSpectators();
            plugin.getBootstrap().getCustomTablist().applyTablistSort();
            plugin.getBootstrap().getCustomTablist().updateTabNames();
            plugin.getBootstrap().getPlayerNameTags().updateNameTags();
        });
    }
}
