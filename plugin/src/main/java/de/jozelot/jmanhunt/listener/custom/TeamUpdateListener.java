package de.jozelot.jmanhunt.listener.custom;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.event.ManhuntTeamAssignEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TeamUpdateListener implements Listener {

    private final JManhunt plugin;

    public TeamUpdateListener(JManhunt plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTeamUpdate(ManhuntTeamAssignEvent event) {
        plugin.getBootstrap().getSpectatorTab().updateSpectators();
        plugin.getBootstrap().getCustomTablist().applyTablistSort();
        plugin.getBootstrap().getCustomTablist().updateTabNames();
        plugin.getBootstrap().getPlayerNameTags().updateNameTags();
    }
}
