package de.jozelot.jmanhunt.game;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.game.GameState;
import de.jozelot.jmanhunt.api.game.ManhuntEndReason;
import de.jozelot.jmanhunt.api.game.PhaseManager;
import de.jozelot.jmanhunt.api.minecraft.Weather;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import de.jozelot.jmanhunt.player.ManhuntPlayerImpl;
import de.jozelot.jmanhunt.player.ManhuntPlayerManagerImpl;
import de.jozelot.jmanhunt.utility.WorldUtils;
import org.bukkit.GameMode;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitTask;

public class PhaseManagerImpl implements PhaseManager {

    private final JManhunt plugin;
    private final GameManagerImpl gameManager;
    private final ManhuntPlayerManagerImpl playerManager;
    private BukkitTask pauseTask;

    public PhaseManagerImpl(JManhunt plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getBootstrap().getGameManager();
        this.playerManager = plugin.getBootstrap().getManhuntPlayerManager();
    }

    protected void handleStateChange(GameState state) {
        boolean canclePauseTask = true;
        switch (state) {
            case SETUP -> {
                plugin.getBootstrap().getTimerManager().stop();
            }
            case PRE_GAME -> {
                plugin.getBootstrap().getTimerManager().stop();
            }
            case RUNNING -> {
                plugin.getServer().getServerTickManager().setFrozen(false);
                playerManager.getRunners().stream().filter(p -> !p.isOnline()).forEach(ManhuntPlayer::eliminate);
                playerManager.getPlayersWithoutTeam().forEach(p -> p.setTeam(ManhuntTeam.SPECTATOR));
                playerManager.getSpectators().forEach(p -> p.getPlayer().setGameMode(GameMode.SPECTATOR));
                plugin.getBootstrap().getTimerManager().start();
            }
            case PAUSE -> {
                canclePauseTask = false;
                if (plugin.getBootstrap().getConfigManager().isPauseFreezeGame()) {
                    plugin.getServer().getServerTickManager().setFrozen(true);
                }
                plugin.getBootstrap().getTimerManager().stop();
            }
            case ENDED -> {
                plugin.getBootstrap().getTimerManager().stop();
            }
        }

        if (canclePauseTask && pauseTask != null) pauseTask.cancel();
    }

    @Override
    public boolean isSetup() {
        return plugin.getBootstrap().getGameManager().getGameState() == GameState.SETUP;
    }

    @Override
    public boolean isPreGame() {
        return plugin.getBootstrap().getGameManager().getGameState() == GameState.PRE_GAME;
    }

    @Override
    public boolean isRunning() {
        return plugin.getBootstrap().getGameManager().getGameState() == GameState.RUNNING;
    }

    @Override
    public boolean isEnded() {
        return plugin.getBootstrap().getGameManager().getGameState() == GameState.ENDED;
    }

    @Override
    public boolean isPaused() {
        return plugin.getBootstrap().getGameManager().getGameState() == GameState.PAUSE;
    }

    @Override
    public void setSetup() {
        gameManager.setGameState(GameState.SETUP);
    }

    @Override
    public void open() {
        gameManager.setGameState(GameState.PRE_GAME);
    }

    @Override
    public void close() {
        gameManager.setGameState(GameState.SETUP);
    }

    @Override
    public void start() {
        if (!gameManager.setGameState(GameState.RUNNING)) return;
        if (plugin.getBootstrap().getConfigManager().resetWeatherTimeOnStart()) {
            var world = plugin.getServer().getWorld("world");
            WorldUtils.changeTime(world, plugin.getBootstrap().getConfigManager().getDefaultTime());
            WorldUtils.changeWeather(world, plugin.getBootstrap().getConfigManager().getDefaultWeather().name().toLowerCase());
        }

    }

    @Override
    public void pause() {
        gameManager.setGameState(GameState.PAUSE);
        pauseTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            end(ManhuntEndReason.MANHUNT_CANCELED);
        }, plugin.getBootstrap().getConfigManager().getEndManhuntAtPause() * 20L);
    }

    @Override
    public void resume() {
        gameManager.setGameState(GameState.RUNNING);
    }


    @Override
    public void end(ManhuntEndReason reason) {
        gameManager.setGameState(GameState.ENDED);
        gameManager.setEndReason(reason);
    }

    @Override
    public boolean isProtected() {
        return isSetup() || isPreGame() || isPaused();
    }

    public boolean canAddToTeam(ManhuntTeam team) {
        if (team == ManhuntTeam.SPECTATOR) return true;
        GameState currentState = plugin.getBootstrap().getGameManager().getGameState();
        if (currentState == GameState.SETUP || currentState == GameState.PRE_GAME) return true;
        return false;
    }

    public boolean canRemoveFromTeam(ManhuntTeam team) {
        if (team == ManhuntTeam.SPECTATOR) return true;
        GameState currentState = plugin.getBootstrap().getGameManager().getGameState();
        if (currentState == GameState.SETUP || currentState == GameState.PRE_GAME || currentState == GameState.PAUSE) return true;
        return false;
    }
}
