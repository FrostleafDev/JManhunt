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
    private boolean canPlayersJoinDuringSetup;
    private String storageMethod;

    private String mysqlHost;
    private int mysqlPort;
    private String mysqlDatabase;
    private String mysqlUser;
    private String mysqlPassword;

    private String databasePrefix;

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
        canPlayersJoinDuringSetup = plugin.getConfig().getBoolean("can-players-join-during-setup", false);
        storageMethod = plugin.getConfig().getString("storage", "SQLITE");

        if (storageMethod.equalsIgnoreCase("MYSQL")) {
            mysqlHost = plugin.getConfig().getString("mysql.host");
            mysqlPort = plugin.getConfig().getInt("mysql.port");
            mysqlDatabase = plugin.getConfig().getString("mysql.database");
            mysqlUser = plugin.getConfig().getString("mysql.user");
            mysqlPassword = plugin.getConfig().getString("mysql.password");
            databasePrefix = plugin.getConfig().getString("database-prefix", "JM_");
        }
    }

    public String getLocale() {
        return locale;
    }

    public boolean canPlayersJoinDuringSetup() {
        return canPlayersJoinDuringSetup;
    }

    public String getStorageMethod() {
        return storageMethod;
    }


    public String getMysqlHost() {
        return mysqlHost;
    }

    public int getMysqlPort() {
        return mysqlPort;
    }

    public String getMysqlDatabase() {
        return mysqlDatabase;
    }

    public String getMysqlUser() {
        return mysqlUser;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public String getDatabasePrefix() {
        return databasePrefix;
    }
}
