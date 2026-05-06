package de.jozelot.jmanhunt.player.tablist;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import de.jozelot.jmanhunt.utility.ReplaceUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomTablist {

    private final JManhunt plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public CustomTablist(JManhunt plugin) {
        this.plugin = plugin;
    }

    public void applyTablist(ManhuntPlayer player) {
        ManhuntTeam team = player.getTeam();

        var config = plugin.getBootstrap().getConfigManager().getTablist();

        List<String> rawHeader;
        List<String> rawFooter;

        switch (player.getTeam()) {
            case HUNTER -> {
                rawHeader = config.getHunterHeader();
                rawFooter = config.getHunterFooter();
            }
            case RUNNER -> {
                rawHeader = config.getRunnerHeader();
                rawFooter = config.getRunnerFooter();
            }
            default -> {
                rawHeader = config.getSpectatorHeader();
                rawFooter = config.getSpectatorFooter();
            }
        }

        Component finalHeader = buildComponent(rawHeader, player);
        Component finalFooter = buildComponent(rawFooter, player);

        player.getPlayer().sendPlayerListHeaderAndFooter(finalHeader, finalFooter);
    }

    public void clearTablist(ManhuntPlayer player) {
        player.getPlayer().sendPlayerListHeaderAndFooter(
                Component.empty(),
                Component.empty()
        );
    }

    public void updateTabName(ManhuntPlayer player) {
        ManhuntTeam team = player.getTeam();
        String teamColor = "<dark_gray>";

        var teamConf = plugin.getBootstrap().getConfigManager().getTeam();

        switch (team) {
            case ManhuntTeam.HUNTER -> teamColor = teamConf.getHunter().getColor();
            case ManhuntTeam.RUNNER -> teamColor = teamConf.getRunner().getColor();
            case ManhuntTeam.SPECTATOR -> teamColor = teamConf.getSpectator().getColor();
            case ManhuntTeam.NONE -> teamColor = teamConf.getNone().getColor();
        }

        String teamName = teamColor + plugin.getBootstrap().getLangManager().format("teams." + team.name().toLowerCase(), null);
        Component tabName = mm.deserialize(plugin.getBootstrap().getConfigManager().getTeamPrefix().getFormat().replace("{team}", teamName).replace("{player_name}", player.getPlayer().getName()));
        player.getPlayer().playerListName(tabName);
    }

    public void clearTabName(ManhuntPlayer player) {
        player.getPlayer().playerListName(mm.deserialize(player.getPlayer().getName()));
    }

    private Component buildComponent(List<String> lines, ManhuntPlayer player) {
        String joined = lines.stream()
                .map(line -> ReplaceUtils.replacePlaceholders(line, plugin, player))
                .collect(Collectors.joining("\n"));

        return mm.deserialize(joined);
    }

    public void sortTablistByTeam(ManhuntPlayer player) {
        var config = plugin.getBootstrap().getConfigManager().getTablist();

        if (!config.getTeamSorting().isEnabled()) {
            player.getPlayer().setPlayerListOrder(0);
            return;
        }

        List<String> order = config.getTeamSorting().getOrder();
        String currentTeamName = player.getTeam().name();

        int index = order.indexOf(currentTeamName);

        int priority = (index != -1) ? (index + 1) : 999;

        player.getPlayer().setPlayerListOrder(priority);
    }

    public void sortTablistDefault(ManhuntPlayer player) {
        player.getPlayer().setPlayerListOrder(0);
    }
}
