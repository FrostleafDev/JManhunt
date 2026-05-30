package de.jozelot.jmanhunt.core.dependencies;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.core.JManhuntBootstrap;
import org.bstats.bukkit.Metrics;


public class bStats {

    private final JManhunt plugin;

    public bStats(JManhunt plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Metrics metrics = new Metrics(plugin, JManhuntBootstrap.BSTATS_ID);

        // TODO: Custom charts
        createCharts();
    }

    public void createCharts() {

    }
}
