package de.jozelot.jmanhunt.listener;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    private final JManhunt plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ChatListener(JManhunt plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        String messageRaw = PlainTextComponentSerializer.plainText().serialize(event.message());

        if (messageRaw.startsWith("/")) return;
        if (!plugin.getBootstrap().getConfigManager().getCustomChat().isEnabled()) return;

        String chatFormat = plugin.getBootstrap().getConfigManager().getCustomChat().getChatFormat();

        Player player = event.getPlayer();

        ManhuntPlayer mPlayer = plugin.getBootstrap().getManhuntPlayerManager().getPlayer(player);
        ManhuntTeam team = mPlayer.getTeam();
        String teamColor = "<dark_gray>";

        var teamConf = plugin.getBootstrap().getConfigManager().getTeam();

        switch (team) {
            case ManhuntTeam.HUNTER -> teamColor = teamConf.getHunter().getColor();
            case ManhuntTeam.RUNNER -> teamColor = teamConf.getRunner().getColor();
            case ManhuntTeam.SPECTATOR -> teamColor = teamConf.getSpectator().getColor();
            case ManhuntTeam.NONE -> teamColor = teamConf.getNone().getColor();
        }

        String teamName = teamColor + plugin.getBootstrap().getLangManager().format("teams." + team.name().toLowerCase(), null);

        event.renderer((source, sourceDisplayName, message, viewer) -> {
            String rawMessage = PlainTextComponentSerializer.plainText().serialize(message);

            // ONLY ALLOW MINIMESSAGE TAGS IF THE PLAYER HAS THE PERMISSION
            String processedMessage = player.hasPermission("jmanhunt.chat.minimessage")
                    ? rawMessage
                    : mm.escapeTags(rawMessage);

            String finalMessage = chatFormat
                    .replace("{team}", teamName)
                    .replace("{player_name}", player.getName())
                    .replace("{message}", processedMessage);

            return mm.deserialize(finalMessage);
        });
    }
}
