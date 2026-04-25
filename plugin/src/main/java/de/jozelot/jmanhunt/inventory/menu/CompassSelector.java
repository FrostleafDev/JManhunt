package de.jozelot.jmanhunt.inventory.menu;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.inventory.menu.InventoryType;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.utility.PlaySoundUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class CompassSelector extends Menu{

    private final JManhunt plugin;

    public CompassSelector(JManhunt plugin) {
        super(plugin, calculateSize(plugin), plugin.getBootstrap().getLangManager().format("menu-compass-selector-title", null));
        this.plugin = plugin;
    }

    private static int calculateSize(JManhunt plugin) {
        int runnerCount = (int) plugin.getBootstrap().getManhuntPlayerManager().getRunners()
                .stream()
                .filter(ManhuntPlayer::isOnline)
                .count();
        int neededSlots = ( (runnerCount + 17) / 9 ) * 9;

        return Math.max(27, Math.min(54, neededSlots));
    }

    @Override
    public void setupItems(ManhuntPlayer player, InventoryType previousInventory) {
        List<ManhuntPlayer> runners = plugin.getBootstrap().getManhuntPlayerManager().getRunners().stream().filter(ManhuntPlayer::isOnline).toList();

        for (int i = 0; i < runners.size(); i++) {
            if (i + 9 > 54) break;
            ManhuntPlayer runner = runners.get(i);
            Player rPlayer = runner.getPlayer();

            setPlayer(runner, i + 9,player.getTracking().get() == runner );
        }
        setFiller(inventory.getSize());
        setBackButton(inventory.getSize() - 1, player, null);
    }

    private void setPlayer(ManhuntPlayer runner, int slot, boolean isCurrentTarget) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);

        item.editMeta(meta -> {
            meta.displayName(mm.deserialize("<!italic>" + (isCurrentTarget ? "<gray>" : "<green>") + runner.getPlayer().getName()));

            meta.getPersistentDataContainer().set(
                    new org.bukkit.NamespacedKey(plugin, "target_uuid"),
                    org.bukkit.persistence.PersistentDataType.STRING,
                    runner.getUniqueId().toString()
            );
        });

        setItem(slot, item, (user, event) -> {
            if (isCurrentTarget) {
                PlaySoundUtils.playError(user.getPlayer(), plugin);
                return;
            }

            String uuidString = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(
                    new org.bukkit.NamespacedKey(plugin, "target_uuid"),
                    org.bukkit.persistence.PersistentDataType.STRING
            );

            if (uuidString != null) {
                UUID targetUuid = java.util.UUID.fromString(uuidString);
                ManhuntPlayer target = plugin.getBootstrap().getManhuntPlayerManager().getPlayer(targetUuid);

                if (target != null) {
                    user.setTracking(target);
                    PlaySoundUtils.playPling(user.getPlayer(), plugin);
                    user.getPlayer().closeInventory();
                }
            }
        });
    }
}
