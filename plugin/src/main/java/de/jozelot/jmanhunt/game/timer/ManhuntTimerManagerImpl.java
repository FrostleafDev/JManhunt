package de.jozelot.jmanhunt.game.timer;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.game.timer.ManhuntTimer;
import de.jozelot.jmanhunt.api.game.timer.ManhuntTimerManager;

public class ManhuntTimerManagerImpl implements ManhuntTimerManager {

    private final JManhunt plugin;
    private ManhuntTimer timer;

    public ManhuntTimerManagerImpl(JManhunt plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        timer = new ManhuntTimerImpl(plugin.getBootstrap().getPhaseManager().isRunning(), plugin.getBootstrap().getMassManager().getTimer());
    }

    public void save() {
        plugin.getBootstrap().getMassManager().saveTimer(timer.getElapsedSeconds());
    }

    @Override
    public ManhuntTimer getTimer() {
        return timer;
    }
}
