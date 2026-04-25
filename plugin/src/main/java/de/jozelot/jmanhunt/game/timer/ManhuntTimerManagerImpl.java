package de.jozelot.jmanhunt.game.timer;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.game.GameState;
import de.jozelot.jmanhunt.api.game.timer.ManhuntTimer;
import de.jozelot.jmanhunt.api.game.timer.ManhuntTimerManager;
import de.jozelot.jmanhunt.storage.ConfigManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.scheduler.BukkitTask;

public class ManhuntTimerManagerImpl implements ManhuntTimerManager {

    private final JManhunt plugin;
    private final ConfigManager configManager;
    private final ConfigManager.Timer conf;
    private ManhuntTimer timer;
    private BukkitTask timerTask;
    private BukkitTask actionbarTask;

    private final MiniMessage mm = MiniMessage.miniMessage();

    public ManhuntTimerManagerImpl(JManhunt plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getBootstrap().getConfigManager();
        this.conf = configManager.getTimer();
    }

    public void setup() {
        timer = new ManhuntTimerImpl(
                plugin.getBootstrap().getPhaseManager().isRunning(),
                plugin.getBootstrap().getMassManager().getTimer()
        );
        if (plugin.getBootstrap().getPhaseManager().isRunning()) start();

        if (plugin.getBootstrap().getConfigManager().getTimer().isEnabled()) startActionbar();
    }

    public void save() {
        plugin.getBootstrap().getMassManager().saveTimer(timer.getElapsedSeconds());
    }

    public void start() {
        if (timerTask != null) {
            timerTask.cancel();
        }

        timerTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            var phaseManager = plugin.getBootstrap().getPhaseManager();

            if (phaseManager.isRunning()) {
                if (timer instanceof ManhuntTimerImpl impl) {
                    impl.tick();
                }
            }

        }, 20L, 20L);
    }

    public void stop() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        save();
    }

    public void startActionbar() {
        if (actionbarTask != null) {
            actionbarTask.cancel();
        }

        actionbarTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::updateActionbar, 20L, 20L);
    }

    public void stopActionbar() {
        if (actionbarTask != null) {
            actionbarTask.cancel();
            actionbarTask = null;
        }
    }

    public void updateActionbar() {
        String text = "<red>Plugin error...";

        if (timer.getElapsedSeconds() < 60) text = timer.format(conf.getFormatS());
        else if (timer.getElapsedSeconds() < 3600) text = timer.format(conf.getFormatMS());
        else text = timer.format(conf.getFormatHMS());

        if (plugin.getBootstrap().getPhaseManager().isPaused()) text = timer.format(conf.getFormatPause());
        if (plugin.getBootstrap().getPhaseManager().isSetup() || plugin.getBootstrap().getPhaseManager().isPreGame() || plugin.getBootstrap().getPhaseManager().isEnded()) {
            text = timer.format(conf.getFormatNotRunning()).replace("{state}", plugin.getBootstrap().getGameManager().getGameState().getName());
        }

        String finalText = text;
        plugin.getServer().getOnlinePlayers().forEach(p -> p.sendActionBar(mm.deserialize(finalText)));
    }

    @Override
    public ManhuntTimer getTimer() {
        return timer;
    }
}
