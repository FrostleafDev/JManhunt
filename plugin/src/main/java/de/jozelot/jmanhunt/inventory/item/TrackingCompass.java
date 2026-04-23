package de.jozelot.jmanhunt.inventory.item;

import de.jozelot.jmanhunt.api.inventory.item.ManhuntItem;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TrackingCompass extends ManhuntItem {

    @Override
    public ItemStack getItemStack() {
        ItemStack item = new ItemStack()
        return null;
    }

    @Override
    public String getId() {
        return "tracking_compass";
    }

    @Override
    public void handleInteract(PlayerInteractEvent event) {

    }
}
