package de.jozelot.jmanhunt.player.tablist;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerNameTags {

    private final JManhunt plugin;

    public PlayerNameTags(JManhunt plugin) {
        this.plugin = plugin;
    }

    public void updateNameTag(ManhuntPlayer player) {
        Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();
        String teamId = "tm_" + player.getPlayer().getName();
        Team team = board.getTeam(teamId);

        if (team == null) {
            team = board.registerNewTeam(teamId);
        }

        var config = plugin.getBootstrap().getConfigManager();
        var teamConf = config.getTeam();

        String teamColor = switch (player.getTeam()) {
            case HUNTER -> teamConf.getHunter().getColor();
            case RUNNER -> teamConf.getRunner().getColor();
            case SPECTATOR -> teamConf.getSpectator().getColor();
            case NONE -> teamConf.getNone().getColor();
        };

        String teamDisplayName = plugin.getBootstrap().getLangManager().format("teams." + player.getTeam().name().toLowerCase(), null);
        String format = config.getTeamPrefix().getFormat();

        String fullFormat = format.replace("{team}", teamColor + teamDisplayName);
        Component fullComponent = MiniMessage.miniMessage().deserialize(fullFormat);
        TextColor extractedColor = fullComponent.color();
        if (!fullComponent.children().isEmpty()) {
            extractedColor = fullComponent.children().get(0).color();
        }
        if (extractedColor != null) {
            team.color(NamedTextColor.nearestTo(extractedColor));
        }

        String[] parts = format.split("\\{player_name}");

        if (parts.length > 0) {
            String prefixRaw = parts[0].replace("{team}", teamColor + teamDisplayName);
            team.prefix(MiniMessage.miniMessage().deserialize(prefixRaw));
        } else {
            team.prefix(Component.empty());
        }

        if (parts.length > 1) {
            String suffixRaw = parts[1].replace("{team}", teamColor + teamDisplayName);
            team.suffix(MiniMessage.miniMessage().deserialize(suffixRaw));
        } else {
            team.suffix(Component.empty());
        }

        if (!team.hasEntry(player.getPlayer().getName())) {
            team.addEntry(player.getPlayer().getName());
        }
    }

    public void clearNameTag(ManhuntPlayer player) {
        Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();
        String teamId = "tm_" + player.getPlayer().getName();
        Team team = board.getTeam(teamId);

        if (team != null) {
            team.unregister();
        }
    }
    public void cleanupTeams() {
        Scoreboard board = plugin.getServer().getScoreboardManager().getMainScoreboard();
        for (Team team : board.getTeams()) {
            if (team.getName().startsWith("tm_")) {
                team.unregister();
            }
        }
    }
}
