package de.jozelot.jmanhunt.listener.mechanic;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class StartProtection implements Listener {

    private final JManhunt plugin;

    public StartProtection(JManhunt plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ManhuntPlayer mPlayer = plugin.getBootstrap().getManhuntPlayerManager().getPlayer(player);
        if (mPlayer.getTeam() != ManhuntTeam.RUNNER) return;

        if (System.currentTimeMillis() < plugin.getBootstrap().getPhaseManager().getStartProtectionEnd()) {
            event.setCancelled(true);
        }
    }
}
