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
    private BukkitTask pauseTask;
    private long startProtectionEnd = 0;

    public PhaseManagerImpl(JManhunt plugin) {
        this.plugin = plugin;
    }

    protected void handleStateChange(GameState state) {
        var playerManager = plugin.getBootstrap().getManhuntPlayerManager();
        boolean canclePauseTask = true;
        switch (state) {
            case SETUP -> {
                plugin.getBootstrap().getTimerManager().stop();
                plugin.getServer().getServerTickManager().setFrozen(false);
            }
            case PRE_GAME -> {
                plugin.getBootstrap().getTimerManager().stop();
                plugin.getServer().getServerTickManager().setFrozen(false);
            }
            case RUNNING -> {
                plugin.getBootstrap().getManhuntPlayerManager().getHunters().forEach(ManhuntPlayer::giveCompass);
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
                plugin.getServer().getServerTickManager().setFrozen(false);
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
        plugin.getBootstrap().getGameManager().setGameState(GameState.SETUP);
    }

    @Override
    public void open() {
        plugin.getBootstrap().getGameManager().setGameState(GameState.PRE_GAME);
    }

    @Override
    public void close() {
        plugin.getBootstrap().getGameManager().setGameState(GameState.SETUP);
    }

    @Override
    public void start() {
        if (!plugin.getBootstrap().getGameManager().setGameState(GameState.RUNNING)) return;
        if (plugin.getBootstrap().getConfigManager().resetWeatherTimeOnStart()) {
            var world = plugin.getServer().getWorld("world");
            WorldUtils.changeTime(world, plugin.getBootstrap().getConfigManager().getDefaultTime());
            WorldUtils.changeWeather(world, plugin.getBootstrap().getConfigManager().getDefaultWeather().name().toLowerCase());
        }
        startProtectionEnd = System.currentTimeMillis() + plugin.getBootstrap().getConfigManager().getStartProtection() * 1000L;
    }

    @Override
    public void pause() {
        plugin.getBootstrap().getGameManager().setGameState(GameState.PAUSE);
        pauseTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            end(ManhuntEndReason.MANHUNT_CANCELED);
        }, plugin.getBootstrap().getConfigManager().getEndManhuntAtPause() * 20L);
    }

    @Override
    public void resume() {
        plugin.getBootstrap().getGameManager().setGameState(GameState.RUNNING);
    }


    @Override
    public void end(ManhuntEndReason reason) {
        plugin.getBootstrap().getGameManager().setGameState(GameState.ENDED);
        plugin.getBootstrap().getGameManager().setEndReason(reason);
    }

    @Override
    public boolean isProtected() {
        return !isRunning();
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

    public long getStartProtectionEnd() {
        return startProtectionEnd;
    }
}
