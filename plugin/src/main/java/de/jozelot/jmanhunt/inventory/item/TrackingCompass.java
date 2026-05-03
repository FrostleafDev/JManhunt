package de.jozelot.jmanhunt.inventory.item;

import com.google.common.io.ByteArrayDataOutput;
import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.inventory.item.ManhuntItem;
import de.jozelot.jmanhunt.api.inventory.menu.InventoryType;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.ManhuntTeam;
import de.jozelot.jmanhunt.utility.PlaySoundUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.*;

public class TrackingCompass extends ManhuntItem {

    private final JManhunt plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    private final Map<UUID, Long> cooldown = new HashMap<>();

    public TrackingCompass(JManhunt plugin) {
        this.plugin = plugin;

        setMetaUpdater((meta, hunter) -> {
            ManhuntPlayer mp = plugin.getBootstrap().getManhuntPlayerManager().getPlayer(hunter.getUniqueId());

            String targetName = mp.getTracking()
                    .map(ManhuntPlayer::getPlayer)
                    .map(Player::getName)
                    .orElse("None");

            mp.getTracking().ifPresent(target -> {
                if (target.getPlayer().getWorld().equals(hunter.getWorld()) && meta instanceof CompassMeta compassMeta) {
                    compassMeta.setLodestoneTracked(false);
                    compassMeta.setLodestone(target.getPlayer().getLocation());
                }
            });

            meta.displayName(mm.deserialize("<!italic>" + plugin.getBootstrap().getLangManager().format("item-tracking-compass.name", Map.of("tracking_player_name", targetName))));

            List<Component> lore = new ArrayList<>();

            for (String line : plugin.getBootstrap().getLangManager().formatList("item-tracking-compass.lore", null)) {
                lore.add(mm.deserialize("<!italic>" + line.replace("{tracking_player_name}", targetName)));
            }

            meta.lore(lore);
            return meta;
        });

        ManhuntItem.registerItem(this);
    }

    @Override
    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.getMaterial(plugin.getBootstrap().getConfigManager().getCompass().getItem()));
        applyItemId(item);
        System.out.println(item);
        item.editMeta(meta -> {
            meta.displayName(mm.deserialize("<!italic>" + plugin.getBootstrap().getLangManager().format("item-tracking-compass.name", Map.of("tracking_player_name", plugin.getBootstrap().getLangManager().format("none", null)))));

            List<Component> lore = new ArrayList<>();

            for (String line : plugin.getBootstrap().getLangManager().formatList("item-tracking-compass.lore", null)) {
                lore.add(mm.deserialize("<!italic>" + line.replace("{tracking_player_name}", plugin.getBootstrap().getLangManager().format("none", null))));
            }

            meta.lore(lore);
        });
        return item;
    }

    @Override
    public String getId() {
        return "TRACKING_COMPASS";
    }

    @Override
    public void handleInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ManhuntPlayer mPlayer = plugin.getBootstrap().getManhuntPlayerManager().getPlayer(player);
        Material material = event.getMaterial();

        if (event.getAction().isRightClick()) {
            PlaySoundUtils.playPling(player, plugin);
            mPlayer.openInventory(InventoryType.COMPASS_SELECTOR);
            return;
        }

        if (player.hasCooldown(material) || mPlayer.getTeam() != ManhuntTeam.HUNTER) {
            return;
        }
        if (event.getAction().isLeftClick()) {
            ItemStack item = event.getItem();
            if (item == null) return;

            if (mPlayer.getTracking().isPresent()) {
                applyUpdate(item, event.getPlayer());
                player.setCooldown(material, plugin.getBootstrap().getConfigManager().getCompass().getCooldown() * 20);
                PlaySoundUtils.playPling(player, plugin);
            }
        }
    }

    @Override
    public boolean canBeDropped() {
        return false;
    }

    @Override
    public boolean dropOnDeath() {
        return false;
    }

    @Override
    public boolean canBreakBlocks() {
        return false;
    }

    @Override
    public boolean canBePlaced() {
        return false;
    }

    @Override
    public boolean canBeMovedIntoDifferentInventory() {
        return false;
    }

    @Override
    public boolean canBePutIntoItemFrame() {
        return false;
    }

    @Override
    public boolean canBeUsedToCraft() {
        return false;
    }

    @Override
    public boolean canInteract() {
        return false;
    }
}
