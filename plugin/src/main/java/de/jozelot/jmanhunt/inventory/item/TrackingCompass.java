package de.jozelot.jmanhunt.inventory.item;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TrackingCompass extends ManhuntItem{
    @Override
    public ItemStack getItemStack() {
        return null;
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public void handleInteract(PlayerInteractEvent event) {

    }
}
