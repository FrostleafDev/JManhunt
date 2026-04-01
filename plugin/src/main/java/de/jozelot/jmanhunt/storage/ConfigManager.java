package de.jozelot.jmanhunt.storage;

import de.jozelot.jmanhunt.JManhunt;

import java.util.logging.Level;

public class ConfigManager {

    private final JManhunt plugin;

    public ConfigManager(JManhunt plugin) {
        this.plugin = plugin;
    }

    private static final int CURRENT_CONFIG_VERSION = 1;

    private String locale;

    public boolean load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        int configVersion = plugin.getConfig().getInt("version", 0);

        if (configVersion < CURRENT_CONFIG_VERSION) {
            plugin.getLogger().log(Level.SEVERE, "");
            plugin.getLogger().log(Level.SEVERE, "Too old configuration file!");
            plugin.getLogger().log(Level.SEVERE, "Your config.yml is too old to run with this version of the plugin");
            plugin.getLogger().log(Level.SEVERE, "Required version: " + CURRENT_CONFIG_VERSION + "; Given version: " + configVersion);
            plugin.getLogger().log(Level.SEVERE, "");
            return false;
        }

        loadData();
        return true;
    }

    /**
     * Saves all the config options to the RAM
     */
    private void loadData() {
        locale = plugin.getConfig().getString("locale", "en").toLowerCase().trim();

    }

    public String getLocale() {
        return locale;
    }
}
