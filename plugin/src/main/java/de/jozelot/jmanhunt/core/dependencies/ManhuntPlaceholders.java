package de.jozelot.jmanhunt.core.dependencies;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.game.timer.ManhuntTimer;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.player.ManhuntPlayerImpl;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ManhuntPlaceholders extends PlaceholderExpansion {

    private final JManhunt plugin;

    public ManhuntPlaceholders(JManhunt plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "jmanhunt";
    }

    @Override
    public @NotNull String getAuthor() {
        return "jozelot_";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";
        var bootstrap = plugin.getBootstrap();
        ManhuntPlayerImpl manhuntPlayer = (ManhuntPlayerImpl) bootstrap.getManhuntPlayerManager().getPlayer(player.getUniqueId());

        // %jmanhunt_team%
        if (params.equalsIgnoreCase("team")) {
            return bootstrap.getTeamManager().getTeamNameByTeam(manhuntPlayer.getTeam());
        }

        // %jmanhunt_lives%
        if (params.equalsIgnoreCase("lives")) {
            return String.valueOf(manhuntPlayer.getLives());
        }

        // %jmanhunt_status%
        if (params.equalsIgnoreCase("status")) {
            return bootstrap.getGameManager().getGameState().name();
        }

        // %jmanhunt_active_runners%
        if (params.equalsIgnoreCase("active_runners")) {
            return String.valueOf(bootstrap.getManhuntPlayerManager().getRunners().stream().filter(r -> !r.isEliminated()).toList().size());
        }

        // %jmanhunt_total_runners%
        if (params.equalsIgnoreCase("total_runners")) {
            return String.valueOf(bootstrap.getManhuntPlayerManager().getRunners().size());
        }

        // %jmanhunt_total_hunters%
        if (params.equalsIgnoreCase("total_hunters")) {
            return String.valueOf(bootstrap.getManhuntPlayerManager().getHunters().size());
        }

        // %jmanhunt_total_spectators%
        if (params.equalsIgnoreCase("total_spectators")) {
            return String.valueOf(bootstrap.getManhuntPlayerManager().getSpectators().size());
        }

        // %jmanhunt_timer_%
        if (params.startsWith("timer_")) {
            String timeOption = params.replace("timer_", "");
            ManhuntTimer timer = plugin.getBootstrap().getTimerManager().getTimer();
            long totalSeconds = timer.getElapsedSeconds();

            long d = totalSeconds / 86400;
            long h = (totalSeconds % 86400) / 3600;
            long m = (totalSeconds % 3600) / 60;
            long s = totalSeconds % 60;

            return switch (timeOption) {
                case "s" -> String.valueOf(s);
                case "S" -> String.format("%02d", s);
                case "m" -> String.valueOf(m);
                case "M" -> String.format("%02d", m);
                case "h" -> String.valueOf(h);
                case "H" -> String.format("%02d", h);
                case "d" -> String.valueOf(d);
                case "D" -> String.format("%02d", d);
                case "formatted" -> (h > 0)
                        ? String.format("%02d:%02d:%02d", h, m, s)
                        : String.format("%02d:%02d", m, s);
                default -> null;
            };
        }

        // %jmanhunt_runner_distance%
        if (params.startsWith("runner_distance")) {
            Optional<ManhuntPlayer> trackingOpt = manhuntPlayer.getTracking();

            if (trackingOpt.isEmpty()) return "0";

            ManhuntPlayer tracking = trackingOpt.get();

            if (!manhuntPlayer.isOnline() || !tracking.isOnline()) return "0";

            Player hunter = manhuntPlayer.getPlayer();
            Player runner = tracking.getPlayer();

            if (!hunter.getWorld().equals(runner.getWorld())) {
                return plugin.getBootstrap().getLangManager().format("different-dimension", null);
            }

            double distance = hunter.getLocation().distance(runner.getLocation());

            if (params.endsWith("_int")) {
                return String.valueOf((int) distance);
            }

            return String.format("%.1f", distance);
        }

        // TODO: goal, goal_progress
        return null;
    }
}
