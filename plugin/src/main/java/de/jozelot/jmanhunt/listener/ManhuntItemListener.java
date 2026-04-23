package de.jozelot.jmanhunt.listener;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.inventory.item.ManhuntItem;
import de.jozelot.jmanhunt.utility.PlaySoundUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
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
            manhuntItem.handleInteract(event);
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryClickEvent event) {
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        ManhuntItem mCurrent = getManhuntItemByItemStack(current);
        ManhuntItem mCursor = getManhuntItemByItemStack(cursor);

        boolean cancel = false;
        if (mCurrent != null && !mCurrent.canBeMoved()) cancel = true;
        if (mCursor != null && !mCursor.canBeMoved()) cancel = true;

        if (cancel) {
            event.setCancelled(true);
            PlaySoundUtils.playError(event.getWhoClicked(), plugin);
        }
    }

    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        ManhuntItem mMain = getManhuntItemByItemStack(event.getMainHandItem());
        ManhuntItem mOff = getManhuntItemByItemStack(event.getOffHandItem());

        boolean cancel = false;
        if (mMain != null && !mMain.canBeMoved()) cancel = true;
        if (mOff != null && !mOff.canBeMoved()) cancel = true;

        if (cancel) {
            event.setCancelled(true);
            PlaySoundUtils.playError(event.getPlayer(), plugin);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();

        ManhuntItem manhuntItem = getManhuntItemByItemStack(item);

        if (manhuntItem != null) {
            event.setCancelled(!manhuntItem.canBeDropped());
            if (!manhuntItem.canBeDropped()) PlaySoundUtils.playError(event.getPlayer(), plugin);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        List<ItemStack> drops = event.getDrops();
        Iterator<ItemStack> iterator = drops.iterator();

        while (iterator.hasNext()) {
            ItemStack item = iterator.next();
            ManhuntItem manhuntItem = getManhuntItemByItemStack(item);

            if (manhuntItem != null) {
                if (!manhuntItem.dropOnDeath()) {
                    iterator.remove();
                }
            }
        }
    }


    private ManhuntItem getManhuntItemByItemStack(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        String key = item.getItemMeta().getPersistentDataContainer().get(ManhuntItem.ITEM_ID, PersistentDataType.STRING);
        if (key == null) return null;

        return ManhuntItem.fromId(key);
    }
}
