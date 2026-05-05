package de.jozelot.jmanhunt.utility;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;

public class ReplaceUtils {

    public static String replacePlaceholders(String line, JManhunt plugin, ManhuntPlayer player) {
        // TODO: RUNNER GOAL
        ManhuntTeam team = player.getTeam();
        return line
                .replace("{player_count}", String.valueOf(plugin.getServer().getOnlinePlayers().size()))
                .replace("{game_player_count}", String.valueOf(plugin.getBootstrap().getManhuntPlayerManager().getActiveParticipants().size()))
                .replace("{lives_left}", String.valueOf(player.getLives()))
                .replace("{current_team}", plugin.getBootstrap().getLangManager().format("teams." + team.name().toLowerCase(), null))
                .replace("{runner_count}", String.valueOf(plugin.getBootstrap().getManhuntPlayerManager().getRunners().size()))
                .replace("{hunter_count}", String.valueOf(plugin.getBootstrap().getManhuntPlayerManager().getHunters().size()))
                .replace("{spectator_count}", String.valueOf(plugin.getBootstrap().getManhuntPlayerManager().getSpectators().size()))
                .replace("{runner_goal}", "")
                .replace("{runner_goal_progress}", "")
                .replace("{runner_goal_entity}", "")
                .replace("{team_deaths}", String.valueOf(plugin.getBootstrap().getTeamManager().getAllPlayersFromTeam(team)
                        .stream()
                        .mapToInt(p -> p.getDeaths())
                        .sum()))
                .replace("{player_deaths}", String.valueOf(player.getDeaths()))
                .replace("{player_kills}", String.valueOf(player.getKills()));
    }
}
