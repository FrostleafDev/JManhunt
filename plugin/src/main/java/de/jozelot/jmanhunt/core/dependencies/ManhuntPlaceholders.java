package de.jozelot.jmanhunt.core.dependencies;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.player.ManhuntPlayerImpl;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ManhuntPlaceholders extends PlaceholderExpansion {

    private final JManhunt plugin;

    public ManhuntPlaceholders(JManhunt plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "jozelot_";
    }

    @Override
    public @NotNull String getAuthor() {
        return "jmanhunt";
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

        // TODO: goal, goal_progress
        return null;
    }
}
