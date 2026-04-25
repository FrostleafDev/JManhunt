package de.jozelot.jmanhunt.core.dependencies;

import de.jozelot.jmanhunt.JManhunt;
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

        if (params.equalsIgnoreCase("team")) {
            return plugin.getBootstrap().getManhuntPlayerManager().getPlayer(player.getUniqueId()).getTeam().getName();
        }

        // %jmanhunt_lives%
        if (params.equalsIgnoreCase("lives")) {
            return String.valueOf(plugin.getBootstrap().getGameManager().getLives(player.getUniqueId()));
        }

        return null;
    }
}
