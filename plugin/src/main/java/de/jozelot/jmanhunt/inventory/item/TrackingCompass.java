package de.jozelot.jmanhunt.inventory.item;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.inventory.item.ManhuntItem;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TrackingCompass extends ManhuntItem {

    private final JManhunt plugin;

    public TrackingCompass(JManhunt plugin) {
        this.plugin = plugin;
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.getMaterial(plugin.getBootstrap().getConfigManager().getCompass().getItem()));
        return null;
    }

    @Override
    public String getId() {
        return "TRACKING_COMPASS";
    }

    @Override
    public void handleInteract(PlayerInteractEvent event) {

    }
}
