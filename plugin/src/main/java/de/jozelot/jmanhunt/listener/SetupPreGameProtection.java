package de.jozelot.jmanhunt.listener;

import de.jozelot.jmanhunt.JManhunt;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SetupPreGameProtection implements Listener {

    private final JManhunt plugin;

    public SetupPreGameProtection(JManhunt plugin) {
        this.plugin = plugin;
    }

}
