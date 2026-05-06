package de.jozelot.jmanhunt.core;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.ApiManager;
import de.jozelot.jmanhunt.api.game.GameState;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.core.dependencies.PluginDependencies;
import de.jozelot.jmanhunt.game.GameManagerImpl;
import de.jozelot.jmanhunt.game.PhaseManagerImpl;
import de.jozelot.jmanhunt.game.timer.ManhuntTimerManagerImpl;
import de.jozelot.jmanhunt.inventory.item.ItemManager;
import de.jozelot.jmanhunt.inventory.item.ItemUpdateService;
import de.jozelot.jmanhunt.inventory.menu.MenuManager;
import de.jozelot.jmanhunt.player.ManhuntPlayerManagerImpl;
import de.jozelot.jmanhunt.player.ManhuntTeamManagerImpl;
import de.jozelot.jmanhunt.player.tablist.CustomTablist;
import de.jozelot.jmanhunt.player.tablist.PlayerNameTags;
import de.jozelot.jmanhunt.player.tablist.SpectatorTab;
import de.jozelot.jmanhunt.registry.CommandRegistry;
import de.jozelot.jmanhunt.registry.ListenerRegistry;
import de.jozelot.jmanhunt.storage.ConfigManager;
import de.jozelot.jmanhunt.storage.LangManager;
import de.jozelot.jmanhunt.storage.mass.MassManager;
import de.jozelot.jmanhunt.utility.PluginMessages;
import de.jozelot.jmanhunt.utility.WorldUtils;

import java.io.File;
import java.util.Timer;
import java.util.logging.Level;

public class JManhuntBootstrap {

    private final JManhunt plugin;
    private PluginDependencies pluginDependencies;
    private boolean canShutdownSafely = false;
    private boolean debugMode = false;

    public JManhuntBootstrap(JManhunt plugin) {
        this.plugin = plugin;
    }

    private ConfigManager configManager;
    private LangManager langManager;
    private UpdateManager updateManager;
    private ApiManager apiManager;
    private GameManagerImpl gameManager;
    private PhaseManagerImpl phaseManager;
    private ManhuntTeamManagerImpl teamManager;
    private ManhuntPlayerManagerImpl manhuntPlayerManager;
    private MassManager massManager;
    private ManhuntTimerManagerImpl timerManager;
    private ItemUpdateService itemUpdateService;
    private ItemManager itemManager;
    private MenuManager menuManager;
    private Heartbeat heartbeat;

    private CustomTablist customTablist;
    private PlayerNameTags playerNameTags;
    private SpectatorTab spectatorTab;

    private CommandRegistry commandRegistry;
    private ListenerRegistry listenerRegistry;

    /**
     * Creates all the needed Object Classes for the project
     */
    public void initialize() {
        pluginDependencies = new PluginDependencies(plugin);
        pluginDependencies.checkDependencies();
        configManager = new ConfigManager(plugin);
        langManager = new LangManager(plugin);
        updateManager = new UpdateManager(plugin);
        apiManager = new ApiManager(plugin);
        gameManager = new GameManagerImpl(plugin);
        phaseManager = new PhaseManagerImpl(plugin);
        teamManager = new ManhuntTeamManagerImpl(plugin);
        commandRegistry = new CommandRegistry(plugin);
        listenerRegistry = new ListenerRegistry(plugin);
        manhuntPlayerManager = new ManhuntPlayerManagerImpl(plugin);
        massManager = new MassManager(plugin);
        timerManager = new ManhuntTimerManagerImpl(plugin);
        itemUpdateService = new ItemUpdateService();
        itemManager = new ItemManager(plugin);
        menuManager = new MenuManager(plugin);
        heartbeat = new Heartbeat(plugin);
        customTablist = new CustomTablist(plugin);
        playerNameTags = new PlayerNameTags(plugin);
        spectatorTab = new SpectatorTab(plugin);
    }

    /**
     * Enables all the logic for the plugin in the given order
     */
    public boolean enable() {
        if (!configManager.load()) return false;
        checkDebugMode();
        if (!langManager.load(configManager.getLocale())) return false;
        if (!massManager.load()) return false;
        manhuntPlayerManager.loadAllFromStorage();
        if (configManager.checkForUpdates()) updateManager.checkForUpdates();
        if (!apiManager.setup()) return false;
        gameManager.loadFromStorage();
        commandRegistry.register();
        listenerRegistry.register();
        pluginDependencies.register();
        timerManager.setup();
        itemManager.init();
        menuManager.registerMenus();

        WorldUtils.applyGamerules();
        if (gameManager.getGameState() == GameState.RUNNING) gameManager.setGameState(GameState.PAUSE);
        playerNameTags.cleanupTeams();
        heartbeat.startHeartbeat();
        canShutdownSafely = true;
        return true;
    }

    /**
     * Shuts down every important part of the plugin
     */
    public void shutdown() {
        heartbeat.stopHeartbeat();
        plugin.getLogger().log(Level.INFO, "Plugin shutting down...");
        timerManager.stopActionbar();
        if (!canShutdownSafely) return;
        apiManager.shutdown();
        if (!gameManager.isWiping()) {
            timerManager.stop();
            timerManager.save();
            gameManager.saveToStorage();
            manhuntPlayerManager.saveAllToStorage();
        }
        massManager.getStorage().close();
        playerNameTags.cleanupTeams();
    }

    /**
     * This is the method that gets run when the plugin reloads
     */
    public void reload() {
        heartbeat.stopHeartbeat();
        plugin.getLogger().log(Level.INFO, "Plugin is reloading...");
        configManager.load();
        langManager.load(configManager.getLocale());

        timerManager.stop();
        timerManager.save();
        timerManager.stopActionbar();
        manhuntPlayerManager.saveAllToStorage();
        gameManager.saveToStorage();
        massManager.getStorage().close();

        massManager.getStorage().init();
        gameManager.loadFromStorage();
        manhuntPlayerManager.loadAllFromStorage();
        menuManager.handleReload();
        manhuntPlayerManager.getPlayers().stream().filter(ManhuntPlayer::isOnline).forEach(ManhuntPlayer::removeCompass);

        if (phaseManager.isRunning()) timerManager.start();
        if (configManager.getTimer().isEnabled()) timerManager.startActionbar();
        if (!configManager.getTablist().isEnabled()) {
            manhuntPlayerManager.getPlayers()
                    .stream()
                    .filter(ManhuntPlayer::isOnline)
                    .forEach(p -> customTablist.clearTablist(p));
        };
        if (!configManager.getTeamPrefix().isTab()) {
            manhuntPlayerManager.getPlayers()
                    .stream()
                    .filter(ManhuntPlayer::isOnline)
                    .forEach(p -> customTablist.clearTabName(p));
        }
        if (!configManager.getTeamPrefix().isNametags()) {
            playerNameTags.cleanupTeams();
        }
        if (!configManager.getTablist().getTeamSorting().isEnabled()) {
            manhuntPlayerManager.getPlayers()
                    .stream()
                    .filter(ManhuntPlayer::isOnline)
                    .forEach(p -> customTablist.sortTablistDefault(p));
        }
        spectatorTab.updateSpectators();
        heartbeat.startHeartbeat();
    }

    /**
     * This checks if the plugin is running in the debug mode
     * This enables debug and developer tools and logging
     */
    private void checkDebugMode() {
        File debugFile = new File(plugin.getDataFolder(), ".debug");
        boolean configEnabled = configManager.isDebugMode();

        if (debugFile.exists() && configEnabled) {
            this.debugMode = true;
            PluginMessages.sendDebugWarning();
        }
        // plugin.getLogger().log(Level.INFO, "Config einstellung: " + configEnabled + ". Datei: " + debugFile.exists() + ". Debug Mode: " + debugMode);
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public PluginDependencies getDependencies() {
        return pluginDependencies;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LangManager getLangManager() {
        return langManager;
    }

    public ApiManager getApiManager() {
        return apiManager;
    }

    public GameManagerImpl getGameManager() {
        return gameManager;
    }

    public PhaseManagerImpl getPhaseManager() {
        return phaseManager;
    }

    public ManhuntTeamManagerImpl getTeamManager() {
        return teamManager;
    }

    public ManhuntPlayerManagerImpl getManhuntPlayerManager() {
        return manhuntPlayerManager;
    }

    public MassManager getMassManager() {
        return massManager;
    }

    public UpdateManager getUpdateManager() {
        return updateManager;
    }

    public ManhuntTimerManagerImpl getTimerManager() {
        return timerManager;
    }

    public ItemUpdateService getItemUpdateService() {
        return itemUpdateService;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public CustomTablist getCustomTablist() {
        return customTablist;
    }

    public PlayerNameTags getPlayerNameTags() {
        return playerNameTags;
    }

    public SpectatorTab getSpectatorTab() {
        return spectatorTab;
    }
}
