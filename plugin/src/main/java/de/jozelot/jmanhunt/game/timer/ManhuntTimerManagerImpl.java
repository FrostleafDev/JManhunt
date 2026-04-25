package de.jozelot.jmanhunt.game.timer;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.game.timer.ManhuntTimer;
import de.jozelot.jmanhunt.api.game.timer.ManhuntTimerManager;

public class ManhuntTimerManagerImpl implements ManhuntTimerManager {

    private final JManhunt plugin;
    private ManhuntTimer timer;

    public ManhuntTimerManagerImpl(JManhunt plugin) {
        this.plugin = plugin;
        timer = new ManhuntTimerImpl(true, 0);
    }

    @Override
    public ManhuntTimer getTimer() {
        return timer;
    }
}
