package de.jozelot.jmanhunt.inventory.menu;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.inventory.menu.InventoryType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class MenuManager {

    private JManhunt plugin;

    // Key: InventoryType (Type für das Inventar), Player: Der, der es öffnet, Object: Übergabe Objekt fürs Menu, Menu: Die InventoryHolder Klasse
    private Map<InventoryType, BiFunction<Player, Object, Menu>> menuFactory  = new HashMap<>();

    public MenuManager(JManhunt plugin) {
        this.plugin = plugin;
    }

    public void registerMenus() {
        menuFactory.clear();
        menuFactory.put(InventoryType.COMPASS_SELECTOR, (player, data) -> new CompassSelector(plugin));

        /*menuFactory.put(InventoryType.SERVER_INFO, (player, data) -> new NavigatorMenu(plugin));

        menuFactory.put(InventoryType.PLAYER_INFO, (player, data) -> {
            Player target = (Player) data; // Wir casten das Objekt zum Spieler
            return new PlayerInfoMenu(plugin, target);
        });*/
    }

    public Menu createMenu(InventoryType type, Player player, Object data) {
        if (!menuFactory.containsKey(type)) return null;

        BiFunction<Player, Object, Menu> factory = menuFactory.get(type);
        if (factory == null) return null;
        return factory.apply(player, data);
    }

    public void handleReload() {
        registerMenus();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.closeInventory();
        }
    }
}
