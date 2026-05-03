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

    public void tick() {
        seconds++;
    }

    @Override
    public @NotNull String format(@NotNull String pattern) {
            long d = seconds / 86400;
            long h = (seconds % 86400) / 3600;
            long m = (seconds % 3600) / 60;
            long s = seconds % 60;

        return pattern
                .replace("{d}", String.valueOf(d))
                .replace("{h}", String.valueOf(h))
                .replace("{m}", String.valueOf(m))
                .replace("{s}", String.valueOf(s))
                .replace("{D}", String.format("%02d", d))
                .replace("{H}", String.format("%02d", h))
                .replace("{M}", String.format("%02d", m))
                .replace("{S}", String.format("%02d", s));
    }
}
