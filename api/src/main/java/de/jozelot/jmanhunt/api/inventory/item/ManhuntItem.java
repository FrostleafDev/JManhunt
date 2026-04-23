/*
 * Copyright (c) 2026 jozelot_. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api.inventory.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Represents a custom item within the JManhunt framework.
 * This class serves as a wrapper to link a Bukkit ItemStack with specific logic and behavior.
 */
public abstract class ManhuntItem {

    /**
     * @return The functional Bukkit ItemStack representing this item in-game.
     */
    public abstract ItemStack getItemStack();

    /**
     * @return A unique identifier for this item type (e.g., "tracking_compass").
     */
    public abstract String getId();

    /**
     * Defines the behavior when a player interacts with this item.
     * * @param event The interaction event triggered by the player.
     */
    public abstract void handleInteract(PlayerInteractEvent event);

    /**
     * Checks if a given ItemStack matches this ManhuntItem definition.
     * Currently based on the display name.
     * @param item The item stack to check.
     * @return true if the item matches, false otherwise.
     */
    public boolean isItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        // Safety check: only compare if both items actually have a display name
        if (!item.getItemMeta().hasDisplayName() || !getItemStack().getItemMeta().hasDisplayName()) {
            return false;
        }

        return item.getItemMeta().getDisplayName().equals(getItemStack().getItemMeta().getDisplayName());
    }
}