package de.jozelot.jmanhunt.inventory.item;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.inventory.item.ManhuntItem;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
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

            meta.displayName(mm.deserialize("<!italic>" + plugin.getBootstrap().getConfigManager().getCompass().getName()
                    .replace("{tracking_player_name}", targetName)));

            List<Component> lore = new ArrayList<>();

            for (String line : plugin.getBootstrap().getConfigManager().getCompass().getLore()) {
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

        item.editMeta(meta -> {
            meta.displayName(mm.deserialize("<!italic>" + plugin.getBootstrap().getConfigManager().getCompass().getName()
                    .replace("{tracking_player_name}", plugin.getBootstrap().getLangManager().format("none", null))));

            List<Component> lore = new ArrayList<>();

            for (String line : plugin.getBootstrap().getConfigManager().getCompass().getLore()) {
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

        if (player.hasCooldown(material)) {
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
        if (event.getAction().isRightClick()) {

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
