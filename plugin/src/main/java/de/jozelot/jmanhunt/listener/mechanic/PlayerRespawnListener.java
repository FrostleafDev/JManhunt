package de.jozelot.jmanhunt.listener.mechanic;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    private final JManhunt plugin;

    public PlayerRespawnListener(JManhunt plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        ManhuntPlayer mPlayer = plugin.getBootstrap().getManhuntPlayerManager().getPlayer(player);

        if (mPlayer.getTeam() == ManhuntTeam.HUNTER) mPlayer.giveCompass();
    }
}
