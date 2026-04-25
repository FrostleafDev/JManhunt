/*
 * Copyright (c) 2026 jozelot_. All rights reserved.
 * Project: JManhunt | Module: API
 */
package de.jozelot.jmanhunt.api.inventory.item;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Represents a custom item within the JManhunt framework.
 * This class serves as a wrapper to link a Bukkit ItemStack with specific logic and behavior.
 */
public abstract class ManhuntItem {

    private BiFunction<ItemMeta, Player, ItemMeta> metaUpdater;

    public void setMetaUpdater(BiFunction<ItemMeta, Player, ItemMeta> updater) {
        this.metaUpdater = updater;
    }

    public static final NamespacedKey ITEM_ID = new NamespacedKey("jmanhunt", "item_id");

    private static final Map<String, ManhuntItem> REGISTRY = new HashMap<>();

    public static void registerItem(ManhuntItem item) {
        REGISTRY.put(item.getId(), item);
    }

    public static ManhuntItem fromId(String id) {
        return REGISTRY.get(id);
    }

    protected ItemStack applyItemId(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(ITEM_ID, PersistentDataType.STRING, getId());
            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean canBeMoved() {
        return true;
    }
    public boolean canBeDropped() {
        return true;
    }
    public boolean dropOnDeath() {
        return true;
    }
    public boolean canBePlaced() {
        return true;
    }
    public boolean canBreakBlocks() {
        return true;
    }
    public boolean canBePutIntoItemFrame() {
        return true;
    }
    public boolean canInteract() {
        return true;
    }
    public boolean canBeMovedIntoDifferentInventory() {
        return true;
    }
    public boolean canBeUsedToCraft() {
        return true;
    }

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

    public void applyUpdate(ItemStack item, Player player) {
        if (item == null || metaUpdater == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        ItemMeta updatedMeta = metaUpdater.apply(meta, player);

        item.setItemMeta(updatedMeta);
    }
}