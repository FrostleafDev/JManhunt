package de.jozelot.jmanhunt.game.timer;

import de.jozelot.jmanhunt.api.game.timer.ManhuntTimer;
import de.jozelot.jmanhunt.player.ManhuntPlayerImpl;
import org.jetbrains.annotations.NotNull;

public class ManhuntTimerImpl implements ManhuntTimer {

    private boolean running;
    private long seconds;

    public ManhuntTimerImpl(boolean running, long seconds) {
        this.running = running;
        this.seconds = seconds;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public long getElapsedSeconds() {
        return seconds;
    }

    @Override
    public void setElapsedSeconds(long seconds) {
        this.seconds = seconds;
    }

    @Override
    public long getRemainingSeconds() {
        return 0;
    }

    @Override
    public @NotNull String format(@NotNull String pattern) {
        return "";
    }
}
