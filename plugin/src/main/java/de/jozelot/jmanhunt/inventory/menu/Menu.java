package de.jozelot.jmanhunt.inventory.menu;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.inventory.menu.InventoryType;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public abstract class Menu implements InventoryHolder {

    protected JManhunt plugin;
    protected Inventory inventory;
    private final Map<Integer, BiConsumer<ManhuntPlayer, InventoryClickEvent>> actions = new HashMap<>();
    public static final NamespacedKey MENU_ITEM_KEY = new NamespacedKey("jmanhunt", "menu_item_key");
    protected MiniMessage mm = MiniMessage.miniMessage();

    public Menu(JManhunt plugin, int size, String title) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    public void open(ManhuntPlayer player) {
        open(player, null);
    }

    public void open(ManhuntPlayer player, InventoryType previousInventory) {
        setupItems(player, previousInventory);
        player.getPlayer().openInventory(getInventory());
    }

    public abstract void setupItems(ManhuntPlayer player, InventoryType previousInventory);

    public void setItem(int slot, ItemStack item, BiConsumer<ManhuntPlayer, InventoryClickEvent> action) {
        inventory.setItem(slot, item);
        if (action != null) {
            actions.put(slot, action);
        }
    }

    public void handleClick(int slot, ManhuntPlayer player, InventoryClickEvent event) {
        if (actions.containsKey(slot)) {
            actions.get(slot).accept(player, event);
        }
    }

    public void setBackButton(int slot, ManhuntPlayer rawPlayer, InventoryType previousMenu) {
        ItemStack arrow = new ItemStack(Material.PLAYER_HEAD);

        arrow.editMeta(SkullMeta.class, meta -> {
            meta.displayName(mm.deserialize(plugin.getBootstrap().getLangManager().format("menu-item-back-arrow", null)));
            meta.lore(Collections.emptyList());
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ADDITIONAL_TOOLTIP);

            String b64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19";

            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            profile.setProperty(new ProfileProperty("textures", b64));

            meta.setPlayerProfile(profile);
        });

        setItem(slot, arrow, (user, event) -> {
            if (previousMenu != null) {
                user.openInventory(previousMenu);
            } else {
                user.getPlayer().closeInventory();
            }
            user.playSound(Sound.PLING);
        });
    }

    public void setFiller(int size) {
        ItemStack filler = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);

        filler.editMeta(meta -> {
            meta.displayName(mm.deserialize(""));
            meta.setHideTooltip(true);
        });

        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, filler);
        }
        for (int i = size - 9; i < size; i++) {
            inventory.setItem(i, filler);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
