package de.jozelot.jmanhunt.core;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.ApiManager;
import de.jozelot.jmanhunt.core.dependencies.PluginDependencies;
import de.jozelot.jmanhunt.game.GameManagerImpl;
import de.jozelot.jmanhunt.game.PhaseManagerImpl;
import de.jozelot.jmanhunt.game.timer.ManhuntTimerManagerImpl;
import de.jozelot.jmanhunt.player.ManhuntPlayerManagerImpl;
import de.jozelot.jmanhunt.player.ManhuntTeamManagerImpl;
import de.jozelot.jmanhunt.registry.CommandRegistry;
import de.jozelot.jmanhunt.registry.ListenerRegistry;
import de.jozelot.jmanhunt.storage.ConfigManager;
import de.jozelot.jmanhunt.storage.LangManager;
import de.jozelot.jmanhunt.storage.mass.MassManager;

import java.util.Timer;
import java.util.logging.Level;

public class JManhuntBootstrap {

    private final JManhunt plugin;
    private PluginDependencies pluginDependencies;
    private boolean canShutdownSafely = false;

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
    }

    /**
     * Enables all the logic for the plugin in the given order
     */
    public boolean enable() {
        if (!configManager.load()) return false;
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
        canShutdownSafely = true;
        return true;
    }

    /**
     * Shuts down every important part of the plugin
     */
    public void shutdown() {
        plugin.getLogger().log(Level.INFO, "Plugin shutting down...");
        if (!canShutdownSafely) return;
        apiManager.shutdown();
        if (!gameManager.isWiping()) {
            timerManager.save();
            gameManager.saveToStorage();
            manhuntPlayerManager.saveAllToStorage();
        }
        massManager.getStorage().close();
    }

    /**
     * This is the method that gets run when the plugin reloads
     */
    public void reload() {
        plugin.getLogger().log(Level.INFO, "Plugin is reloading...");
        configManager.load();
        langManager.load(configManager.getLocale());

        manhuntPlayerManager.saveAllToStorage();
        gameManager.saveToStorage();
        massManager.getStorage().close();

        massManager.getStorage().init();
        gameManager.loadFromStorage();
        manhuntPlayerManager.loadAllFromStorage();
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
}
