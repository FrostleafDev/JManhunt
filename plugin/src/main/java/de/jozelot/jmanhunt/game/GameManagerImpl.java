package de.jozelot.jmanhunt.game;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.event.GameStateChangeEvent;
import de.jozelot.jmanhunt.api.game.GameManager;
import de.jozelot.jmanhunt.api.game.GameState;
import de.jozelot.jmanhunt.api.game.ManhuntEndReason;
import de.jozelot.jmanhunt.api.game.PhaseManager;
import de.jozelot.jmanhunt.api.game.timer.ManhuntTimer;
import de.jozelot.jmanhunt.utility.PluginMessages;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Level;

public class GameManagerImpl implements GameManager {

    private final JManhunt plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public GameManagerImpl(JManhunt plugin) {
        this.plugin = plugin;
    }

    private GameState state = GameState.SETUP;
    private ManhuntEndReason endReason = ManhuntEndReason.NONE;
    private boolean isWiping = false;


    @NotNull
    @Override
    public GameState getGameState() {
        return state;
    }


    @Override
    public void setGameState(@NotNull GameState state) {
        GameStateChangeEvent event = new GameStateChangeEvent(this.state, state);

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            plugin.getLogger().info("Game state change to " + state + " was canceled");
            return;
        }

        this.state = state;

        plugin.getBootstrap().getPhaseManager().handleStateChange(state);
    }


    public void loadFromStorage() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            state = plugin.getBootstrap().getMassManager().loadState();
            endReason = plugin.getBootstrap().getMassManager().loadEndReason();
        });
    }

    public void saveToStorage() {
        if (isWiping) {
            plugin.getLogger().info("Skipping save because a wipe is in progress.");
            return;
        }

        final GameState currentState = this.state;
        final ManhuntEndReason currentReason = this.endReason;

        Runnable saveTask = () -> {
            plugin.getBootstrap().getMassManager().saveState(currentState);
            plugin.getBootstrap().getMassManager().saveEndReason(currentReason);
        };

        if (!plugin.isEnabled()) {
            saveTask.run();
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, saveTask);
        }
    }

    public ManhuntEndReason getEndReason() {
        return endReason;
    }

    public void setEndReason(ManhuntEndReason reason) {
        endReason = reason;
    }

    @Override
    public PhaseManager getPhaseManager() {
        return plugin.getBootstrap().getPhaseManager();
    }

    @Override
    public ManhuntTimer getTimer() {
        return null;
    }

    public void wipeSystem() {
        this.isWiping = true;
        plugin.getLogger().log(Level.WARNING, "The plugin is being wiped...");

        File serverRoot = plugin.getDataFolder().getParentFile().getParentFile();
        File resetFlag = new File(serverRoot, "reset_worlds.txt");

        try {
            if (resetFlag.createNewFile()) {
                plugin.getLogger().info("Reset flag was created");
            }
        } catch (java.io.IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldnt create reset flag: ", e);
        }

        plugin.getBootstrap().getManhuntPlayerManager().getPlayers().clear();

        var kickLines = plugin.getBootstrap().getLangManager().formatList("command-manhunt-reset-kick", null);
        var kickMessage = mm.deserialize(String.join("<newline>", kickLines));
        Bukkit.getOnlinePlayers().forEach(p -> p.kick(kickMessage));

        plugin.getBootstrap().getMassManager().clearAllData();

        PluginMessages.sendWipeError(plugin);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
    }

    public boolean isWiping() {
        return isWiping;
    }
}
