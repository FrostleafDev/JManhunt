package de.jozelot.jmanhunt.core.dependencies;

import de.jozelot.jmanhunt.JManhunt;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class PluginDependencies {

    private final JManhunt plugin;
    private boolean isPlaceholderAPIAvailable = false;

    public PluginDependencies(JManhunt plugin) {
        this.plugin = plugin;
    }

    public void checkDependencies() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            isPlaceholderAPIAvailable = true;
            plugin.getLogger().log(Level.INFO, "PlaceholderAPI Support enabled. View config or documentation for usage");
        }
    }

    public void register() {
        if (isPlaceholderAPIAvailable) new ManhuntPlaceholders(plugin).register();
    }

    public boolean isPlaceholderAPI() {
        return isPlaceholderAPIAvailable;
    }
}
