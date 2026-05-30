package de.jozelot.jmanhunt.core.dependencies;

import de.jozelot.jmanhunt.JManhunt;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.logging.Level;

public class PluginDependencyLoader {

    private final JManhunt plugin;
    private boolean isPlaceholderAPIAvailable = false;

    private ManhuntPlaceholders placeholders;
    private Collection<PluginDependency> dependencies;

    public PluginDependencyLoader(JManhunt plugin) {
        this.plugin = plugin;
    }

    public void checkDependencies() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            isPlaceholderAPIAvailable = true;
            plugin.getLogger().log(Level.INFO, "PlaceholderAPI Support enabled. View config or documentation for usage");
        }
    }

    public void register() {
        if (isPlaceholderAPIAvailable) {
            placeholders = new ManhuntPlaceholders(plugin);
            placeholders.register();
        }
        dependencies.add(new bStats(plugin));
        dependencies.forEach(PluginDependency::register);
    }

    public boolean isPlaceholderAPI() {
        return isPlaceholderAPIAvailable;
    }
}
