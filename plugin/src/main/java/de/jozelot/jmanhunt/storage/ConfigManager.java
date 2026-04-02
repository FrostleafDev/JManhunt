package de.jozelot.jmanhunt.storage;

import de.jozelot.jmanhunt.JManhunt;

import java.util.logging.Level;

public class ConfigManager {

    private final JManhunt plugin;
    private final Sounds sounds;

    public ConfigManager(JManhunt plugin) {
        this.plugin = plugin;
        this.sounds = new Sounds();
    }

    private static final int CURRENT_CONFIG_VERSION = 1;

    private String locale;
    private boolean checkForUpdates;
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
        checkForUpdates = plugin.getConfig().getBoolean("check-for-updates", true);
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

        sounds.pling = plugin.getConfig().getString("sounds.pling");
        sounds.success = plugin.getConfig().getString("sounds.success");
        sounds.error = plugin.getConfig().getString("sounds.error");
        sounds.warning = plugin.getConfig().getString("sounds.warning");
        sounds.notify = plugin.getConfig().getString("sounds.notify");
        sounds.experience = plugin.getConfig().getString("sounds.experience");
    }

    public class Sounds {

        private String pling;
        private String success;
        private String error;
        private String notify;
        private String experience;
        private String warning;

        public String getPling() {
            return pling;
        }

        public String getSuccess() {
            return success;
        }

        public String getError() {
            return error;
        }

        public String getNotify() {
            return notify;
        }

        public String getExperience() {
            return experience;
        }

        public String getWarning() {
            return warning;
        }
    }

    public Sounds getSounds() {
        return sounds;
    }

    public String getLocale() {
        return locale;
    }

    public boolean checkForUpdates() {
        return checkForUpdates;
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
