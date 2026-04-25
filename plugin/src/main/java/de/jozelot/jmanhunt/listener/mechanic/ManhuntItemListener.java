package de.jozelot.jmanhunt.listener.mechanic;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.inventory.item.ManhuntItem;
import de.jozelot.jmanhunt.utility.PlaySoundUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Iterator;
import java.util.List;

public class ManhuntItemListener implements Listener {

    private final JManhunt plugin;

    public ManhuntItemListener(JManhunt plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        ManhuntItem manhuntItem = getManhuntItemByItemStack(item);

        if (manhuntItem != null) {
            if (!manhuntItem.canInteract() && event.hasBlock()) {
                event.setCancelled(true);
            }
            manhuntItem.handleInteract(event);
        }
    }

    // PROTECTION STUFF

    @EventHandler
    public void onInventoryMove(InventoryClickEvent event) {
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        ManhuntItem mCurrent = getManhuntItemByItemStack(current);
        ManhuntItem mCursor = getManhuntItemByItemStack(cursor);

        if (event.getClick() == ClickType.NUMBER_KEY) {
            ItemStack hotbarItem = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
            ManhuntItem mHotbar = getManhuntItemByItemStack(hotbarItem);

            if (mHotbar != null) {
                if (!mHotbar.canBeMovedIntoDifferentInventory()) {
                    if (event.getClickedInventory() != null && !event.getClickedInventory().equals(event.getWhoClicked().getInventory())) {
                        cancelClick(event);
                        return;
                    }

                    if (event.getSlotType() == InventoryType.SlotType.CRAFTING) {
                        cancelClick(event);
                        return;
                    }
                }

                if (!mHotbar.canBeMoved()) {
                    cancelClick(event);
                    return;
                }
            }
        }

        if (event.getClick() == ClickType.SWAP_OFFHAND) {
            ItemStack offhandItem = event.getWhoClicked().getInventory().getItemInOffHand();
            ManhuntItem mOffhand = getManhuntItemByItemStack(offhandItem);

            if (mOffhand != null) {
                if (!mOffhand.canBeMovedIntoDifferentInventory()) {
                    if (event.getClickedInventory() != null && !event.getClickedInventory().equals(event.getWhoClicked().getInventory())) {
                        cancelClick(event);
                        return;
                    }
                    if (event.getSlotType() == InventoryType.SlotType.CRAFTING) {
                        cancelClick(event);
                        return;
                    }
                }
                if (!mOffhand.canBeMoved()) {
                    cancelClick(event);
                    return;
                }
            }
        }

        if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.BUNDLE ||
                event.getCursor() != null && event.getCursor().getType() == Material.BUNDLE) {

            if (mCurrent != null && !mCurrent.canBeMovedIntoDifferentInventory() ||
                    mCursor != null && !mCursor.canBeMovedIntoDifferentInventory()) {

                cancelClick(event);
                return;
            }
        }

        if ((mCurrent != null && !mCurrent.canBeMoved()) || (mCursor != null && !mCursor.canBeMoved())) {
            cancelClick(event);
            return;
        }

        if (mCurrent != null && !mCurrent.canBeUsedToCraft()) {
            if (event.getSlotType() == InventoryType.SlotType.CRAFTING) {
                cancelClick(event);
                return;
            }
        }

        if (mCursor != null && !mCursor.canBeMovedIntoDifferentInventory()) {
            if (event.getClickedInventory() != null && !event.getClickedInventory().equals(event.getWhoClicked().getInventory())) {
                cancelClick(event);
                return;
            }
        }

        if (mCurrent != null && !mCurrent.canBeMovedIntoDifferentInventory()) {
            if (event.getSlotType() == InventoryType.SlotType.CRAFTING) {
                cancelClick(event);
                return;
            }

            if (event.isShiftClick()) {
                InventoryType type = event.getView().getTopInventory().getType();

                if (type != InventoryType.CRAFTING && type != InventoryType.PLAYER) {
                    cancelClick(event);
                    return;
                }
            }

            if (event.getClickedInventory() != null && !event.getClickedInventory().equals(event.getWhoClicked().getInventory())) {
                cancelClick(event);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        ManhuntItem mDrag = getManhuntItemByItemStack(event.getOldCursor());
        if (mDrag != null && !mDrag.canBeMovedIntoDifferentInventory()) {
            for (int slot : event.getRawSlots()) {
                if (slot < event.getInventory().getSize() && !event.getInventory().equals(event.getWhoClicked().getInventory())) {
                    event.setCancelled(true);
                    PlaySoundUtils.playError(event.getWhoClicked(), plugin);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
        ManhuntItem mItem = getManhuntItemByItemStack(item);

        if (mItem != null && !mItem.canBePutIntoItemFrame()) {
            if (event.getRightClicked().getType().name().contains("ITEM_FRAME")) {
                event.setCancelled(true);
                PlaySoundUtils.playError(event.getPlayer(), plugin);
            }
        }
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        ManhuntItem manhuntItem = getManhuntItemByItemStack(event.getItemInHand());
        if (manhuntItem != null && !manhuntItem.canBePlaced()) {
            event.setCancelled(true);
            PlaySoundUtils.playError(event.getPlayer(), plugin);
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        // Hinweis: getActiveItem() ist oft für Bogen/Essen. Nutze lieber ItemInMainHand
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        ManhuntItem manhuntItem = getManhuntItemByItemStack(item);

        if (manhuntItem != null && !manhuntItem.canBreakBlocks()) {
            event.setCancelled(true);
            PlaySoundUtils.playError(event.getPlayer(), plugin);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ManhuntItem manhuntItem = getManhuntItemByItemStack(event.getItemDrop().getItemStack());
        if (manhuntItem != null && !manhuntItem.canBeDropped()) {
            event.setCancelled(true);
            PlaySoundUtils.playError(event.getPlayer(), plugin);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Iterator<ItemStack> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            ManhuntItem manhuntItem = getManhuntItemByItemStack(iterator.next());
            if (manhuntItem != null && !manhuntItem.dropOnDeath()) {
                iterator.remove();
            }
        }
    }

    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        ManhuntItem mMain = getManhuntItemByItemStack(event.getMainHandItem());
        ManhuntItem mOff = getManhuntItemByItemStack(event.getOffHandItem());
        if ((mMain != null && !mMain.canBeMoved()) || (mOff != null && !mOff.canBeMoved())) {
            event.setCancelled(true);
            PlaySoundUtils.playError(event.getPlayer(), plugin);
        }
    }

    private void cancelClick(InventoryClickEvent event) {
        event.setCancelled(true);
        PlaySoundUtils.playError(event.getWhoClicked(), plugin);
    }

    private ManhuntItem getManhuntItemByItemStack(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        String key = item.getItemMeta().getPersistentDataContainer().get(ManhuntItem.ITEM_ID, PersistentDataType.STRING);
        return (key == null) ? null : ManhuntItem.fromId(key);
    }
}
