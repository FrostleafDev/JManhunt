package de.jozelot.jmanhunt.inventory.item;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.inventory.item.ManhuntItem;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class ItemUpdateService {

    private final JManhunt plugin;

    public ItemUpdateService(JManhunt plugin) {
        this.plugin = plugin;
    }

    public void updateItemsForPlayer(ManhuntPlayer mPlayer) {
        Player player = mPlayer.getPlayer();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            if (!item.hasItemMeta()) continue;

            String id = item.getItemMeta().getPersistentDataContainer()
                    .get(ManhuntItem.ITEM_ID, PersistentDataType.STRING);

            if (id == null) continue;

            ManhuntItem manhuntItem = ManhuntItem.fromId(id);
            if (manhuntItem != null) {
                manhuntItem.applyUpdate(item, player);
            }
        }
    }

    public void updateItems() {
        plugin.getBootstrap().getManhuntPlayerManager().getHunters().stream().filter(ManhuntPlayer::isOnline).forEach(this::updateItemsForPlayer);
    }
}