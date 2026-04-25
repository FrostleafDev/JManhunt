package de.jozelot.jmanhunt.listener.mechanic;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.inventory.menu.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuClickListener implements Listener {

    private final JManhunt plugin;

    public MenuClickListener(JManhunt plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof Menu menu)) return;

        event.setCancelled(true);

        if (event.getClickedInventory().equals(event.getInventory())) {
            ManhuntPlayer user = plugin.getBootstrap().getManhuntPlayerManager().getPlayer(event.getWhoClicked().getUniqueId());

            if (user != null) {
                menu.handleClick(event.getSlot(), user, event);
            }
        }
    }
}
