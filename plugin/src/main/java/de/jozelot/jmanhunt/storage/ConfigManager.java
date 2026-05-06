package de.jozelot.jmanhunt.storage;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.minecraft.Weather;
import de.jozelot.jmanhunt.inventory.item.CompassUpdate;
import org.bukkit.Server;
import org.bukkit.WeatherType;
import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class ConfigManager {

    private final JManhunt plugin;
    private final Sounds sounds;
    private final TrackingCompass trackingCompass;
    private final ServerList serverList;
    private final Timer timer;
    private final CustomChat customChat;
    private final Team team;
    private final Tablist tablist;
    private final TeamPrefix teamPrefix;

    public ConfigManager(JManhunt plugin) {
        this.plugin = plugin;
        this.sounds = new Sounds();
        this.trackingCompass = new TrackingCompass();
        this.serverList = new ServerList();
        this.timer = new Timer();
        this.customChat = new CustomChat();
        this.team = new Team();
        this.tablist = new Tablist();
        this.teamPrefix = new TeamPrefix();
    }

    private static final int CURRENT_CONFIG_VERSION = 1; // ME: IMPORTANT TO CHANGE
    private boolean debugMode;

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
    private boolean sendCustomJoinLeaveMessages;
    private boolean playerJoinSound;
    private boolean pauseFreezeGame;
    private boolean pauseFreezePlayer;
    private boolean resetWeatherTimeOnStart;
    private Weather defaultWeather;
    private String defaultTime;
    private int endManhuntAtPause;
    private int startProtection;

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
        // DEV CHECKS
        debugMode = plugin.getConfig().getBoolean("debug-mode", false);

        // SYSTEM CONFIGURATION
        locale = plugin.getConfig().getString("locale", "en").toLowerCase().trim();
        checkForUpdates = plugin.getConfig().getBoolean("check-for-updates", true);
        canPlayersJoinDuringSetup = plugin.getConfig().getBoolean("can-players-join-during-setup", false);
        storageMethod = plugin.getConfig().getString("storage", "SQLITE");

        if (storageMethod.equalsIgnoreCase("MYSQL")) {
            mysqlHost = plugin.getConfig().getString("mysql.host");
            mysqlPort = plugin.getConfig().getInt("mysql.port");
            mysqlDatabase = plugin.getConfig().getString("mysql.database");
            mysqlUser = plugin.getConfig().getString("mysql.username");
            mysqlPassword = plugin.getConfig().getString("mysql.password");
            databasePrefix = plugin.getConfig().getString("database-prefix", "JM_");
        }

        // SOUNDS
        sounds.pling = plugin.getConfig().getString("sounds.pling");
        sounds.success = plugin.getConfig().getString("sounds.success");
        sounds.error = plugin.getConfig().getString("sounds.error");
        sounds.warning = plugin.getConfig().getString("sounds.warning");
        sounds.notify = plugin.getConfig().getString("sounds.notify");
        sounds.experience = plugin.getConfig().getString("sounds.experience");

        // TRACKING COMPASS
        trackingCompass.enabled = plugin.getConfig().getBoolean("tracking-compass.enabled", true);
        trackingCompass.item = plugin.getConfig().getString("tracking-compass.item", "COMPASS");
        trackingCompass.cooldown = plugin.getConfig().getInt("tracking-compass.click-cooldown", 3);

        String configValue = plugin.getConfig().getString("tracking-compass.update-interval", "CLICK");
        try {
            trackingCompass.updateInterval = CompassUpdate.valueOf(configValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            trackingCompass.updateInterval = CompassUpdate.CLICK;
            plugin.getLogger().log(Level.WARNING, "Invalid update-interval '" + configValue + "' in config.yml! Defaulting to CLICK.");
        }

        // SERVERLIST MOTD
        serverList.motd.enabled = plugin.getConfig().getBoolean("motd.enabled", false);
        serverList.motd.lines = plugin.getConfig().getStringList("motd.lines");

        // SERVERLIST PLAYER COUNT
        serverList.playerCount.enabled = plugin.getConfig().getBoolean("max-player-count.enabled", true);
        configValue = plugin.getConfig().getString("max-player-count.mode", "DYNAMIC");
        try {
            serverList.playerCount.mode = PlayerCount.valueOf(configValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            serverList.playerCount.mode = PlayerCount.DYNAMIC;
            plugin.getLogger().log(Level.WARNING, "Invalid player-count-mode '" + configValue + "' in config.yml! Defaulting to DYNAMIC.");
        }

        serverList.playerCount.value = plugin.getConfig().getInt("max-player-count.value", 1);

        // TIMER
        timer.enabled = plugin.getConfig().getBoolean("timer-in-actionbar", true);
        timer.formatS = plugin.getConfig().getString("timer-format-s", "<gradient:green:dark_green><b>{s}s");
        timer.formatMS = plugin.getConfig().getString("timer-format-ms", "<gradient:green:dark_green><b>{m}m {s}s");
        timer.formatHMS = plugin.getConfig().getString("timer-format-hms", "<gradient:green:dark_green><b>{h}h {m}m {s}s");
        timer.formatDHMS = plugin.getConfig().getString("timer-format-dhms", "<gradient:green:dark_green><b>{d}d {h}h {m}m {s}s");
        timer.formatPause = plugin.getConfig().getString("timer-format-paused", "<gradient:green:dark_green><b><i>Manhunt paused...");
        timer.formatNotRunning = plugin.getConfig().getString("timer-format-not-running", "<gradient:green:dark_green><b><i>Manhunt {state}");

        // TEAM CONFIG

        team.hunter.color = plugin.getConfig().getString("team-color.hunter");
        team.runner.color = plugin.getConfig().getString("team-color.runner");
        team.spectator.color = plugin.getConfig().getString("team-color.spectator");
        team.none.color = plugin.getConfig().getString("team-color.none");

        // CUSTOM CHAT
        customChat.chatFormat = plugin.getConfig().getString("custom-chat-format", "{team} | {player_name} <dark_gray>» <gray>{message}");
        customChat.enabled = plugin.getConfig().getBoolean("custom-chat", true);

        // TABLIST
        tablist.enabled = plugin.getConfig().getBoolean("tablist.enabled", true);
        tablist.runnerFooter = plugin.getConfig().getStringList("tablist.runner.footer");
        tablist.runnerHeader = plugin.getConfig().getStringList("tablist.runner.header");
        tablist.hunterFooter = plugin.getConfig().getStringList("tablist.hunter.footer");
        tablist.hunterHeader = plugin.getConfig().getStringList("tablist.hunter.header");
        tablist.spectatorFooter = plugin.getConfig().getStringList("tablist.spectator.footer");
        tablist.spectatorHeader = plugin.getConfig().getStringList("tablist.spectator.header");

        tablist.teamSorting.enabled = plugin.getConfig().getBoolean("tablist.team-sorting.enabled", true);
        tablist.teamSorting.order = plugin.getConfig().getStringList("tablist.team-sorting.order");

        // TEAM PREFIX
        teamPrefix.tab = plugin.getConfig().getBoolean("team-prefix.tab", true);
        teamPrefix.nametags = plugin.getConfig().getBoolean("team-prefix.nametags", true);
        teamPrefix.format = plugin.getConfig().getString("team-prefix.format", "{team} | {player_name}");

        // CUSTOM JOIN
        sendCustomJoinLeaveMessages = plugin.getConfig().getBoolean("custom-join-leave-messages", true);
        playerJoinSound = plugin.getConfig().getBoolean("join-sound", true);

        // PAUSE
        pauseFreezeGame = plugin.getConfig().getBoolean("pause-freeze-game", true);
        pauseFreezePlayer = plugin.getConfig().getBoolean("pause-freeze-players", true);

        // TIME WEATHER
        resetWeatherTimeOnStart = plugin.getConfig().getBoolean("reset-weather-time-on-start", false);

        configValue = plugin.getConfig().getString("default-weather", "CLEAR");
        try {
            defaultWeather = Weather.valueOf(configValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            defaultWeather = Weather.CLEAR;
            plugin.getLogger().log(Level.WARNING, "Invalid weather type on l.285 '" + configValue + "' in config.yml! Defaulting to CLEAR.");
        }
        defaultTime = plugin.getConfig().getString("default-time", "DAY");
        endManhuntAtPause = plugin.getConfig().getInt("pause-timeout", 600);

        startProtection = plugin.getConfig().getInt("start-protection", 60);
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

    public class TrackingCompass {

        private boolean enabled;
        private String item;
        private CompassUpdate updateInterval;
        private int cooldown;

        public boolean isEnabled() {
            return enabled;
        }

        public String getItem() {
            return item;
        }

        public CompassUpdate getUpdateInterval() {
            return updateInterval;
        }

        public int getCooldown() {
            return cooldown;
        }
    }

    public class ServerList {

        private final Motd motd = new Motd();
        private final PlayerCount playerCount = new PlayerCount();

        public Motd getMotd() {
            return motd;
        }

        public PlayerCount getPlayerCount() {
            return playerCount;
        }

        public class Motd {

            private boolean enabled;
            private List<String> lines;

            public boolean isEnabled() {
                return enabled;
            }

            public List<String> getLines() {
                return lines;
            }
        }

        public class PlayerCount {
            private boolean enabled;
            private de.jozelot.jmanhunt.storage.PlayerCount mode;
            private int value;

            public boolean isEnabled() {
                return enabled;
            }

            public de.jozelot.jmanhunt.storage.PlayerCount getMode() {
                return mode;
            }

            public int getValue() {
                return value;
            }
        }
    }

    public class Timer {
        private boolean enabled;
        private String formatS;
        private String formatMS;
        private String formatHMS;
        private String formatDHMS;
        private String formatPause;
        private String formatNotRunning;

        public boolean isEnabled() {
            return enabled;
        }

        public String getFormatS() {
            return formatS;
        }

        public String getFormatMS() {
            return formatMS;
        }

        public String getFormatHMS() {
            return formatHMS;
        }

        public String getFormatDHMS() {
            return formatDHMS;
        }

        public String getFormatPause() {
            return formatPause;
        }

        public String getFormatNotRunning() {
            return formatNotRunning;
        }
    }

    public class CustomChat {
        private String chatFormat;
        private boolean enabled;

        public String getChatFormat() {
            return chatFormat;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

    public class Team {
        public class TeamConfig {
            protected String color;

            public String getColor() {
                return color;
            }
        }
        public class Hunter extends TeamConfig {

        }
        public class Runner extends TeamConfig {

        }
        public class Spectator extends TeamConfig {

        }
        public class None extends TeamConfig{

        }

        private Hunter hunter = new Hunter();
        private Runner runner = new Runner();
        private Spectator spectator = new Spectator();
        private None none = new None();

        public Hunter getHunter() {
            return hunter;
        }

        public Runner getRunner() {
            return runner;
        }

        public Spectator getSpectator() {
            return spectator;
        }

        public None getNone() {
            return none;
        }
    }

    public class Tablist {
        private boolean enabled;
        private List<String> runnerHeader;
        private List<String> runnerFooter;
        private List<String> hunterHeader;
        private List<String> hunterFooter;
        private List<String> spectatorHeader;
        private List<String> spectatorFooter;

        private TeamSorting teamSorting = new TeamSorting();

        public class TeamSorting {
            private boolean enabled;
            private List<String> order;

            public boolean isEnabled() {
                return enabled;
            }

            public List<String> getOrder() {
                return order;
            }
        }

        public boolean isEnabled() {
            return enabled;
        }

        public TeamSorting getTeamSorting() {
            return teamSorting;
        }

        public List<String> getRunnerHeader() {
            return runnerHeader;
        }

        public List<String> getRunnerFooter() {
            return runnerFooter;
        }

        public List<String> getHunterHeader() {
            return hunterHeader;
        }

        public List<String> getHunterFooter() {
            return hunterFooter;
        }

        public List<String> getSpectatorHeader() {
            return spectatorHeader;
        }

        public List<String> getSpectatorFooter() {
            return spectatorFooter;
        }
    }

    public class TeamPrefix {
        private boolean tab;
        private boolean nametags;
        private String format;

        public boolean isTab() {
            return tab;
        }

        public boolean isNametags() {
            return nametags;
        }

        public String getFormat() {
            return format;
        }
    }

    public Sounds getSounds() {
        return sounds;
    }

    public TrackingCompass getCompass() {
        return trackingCompass;
    }

    public ServerList getServerList() {
        return serverList;
    }

    public Timer getTimer() {
        return timer;
    }

    public CustomChat getCustomChat() {
        return customChat;
    }

    public Team getTeam() {
        return team;
    }

    public Tablist getTablist() {
        return tablist;
    }

    public TeamPrefix getTeamPrefix() {
        return teamPrefix;
    }

    public boolean isDebugMode() {
        return debugMode;
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

    public boolean sendCustomConnectionMessages() {
        return sendCustomJoinLeaveMessages;
    }

    public boolean playJoinSound() {
        return playerJoinSound;
    }

    public boolean isPauseFreezeGame() {
        return pauseFreezeGame;
    }

    public boolean isPauseFreezePlayer() {
        return pauseFreezePlayer;
    }

    public boolean resetWeatherTimeOnStart() {
        return resetWeatherTimeOnStart;
    }

    public Weather getDefaultWeather() {
        return defaultWeather;
    }

    public String getDefaultTime() {
        return defaultTime;
    }

    public int getEndManhuntAtPause() {
        return endManhuntAtPause;
    }

    public int getStartProtection() {
        return startProtection;
    }
}
