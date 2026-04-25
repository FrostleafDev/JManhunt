package de.jozelot.jmanhunt.inventory.item;

import de.jozelot.jmanhunt.JManhunt;
import java.util.HashMap;
import java.util.Map;

public class ItemManager {

    private final Map<String, TrackingCompass> items = new HashMap<>();
    private final JManhunt plugin;

    public ItemManager(JManhunt plugin) {
        this.plugin = plugin;
    }

    public void init() {
        items.put("TRACKING_COMPASS", new TrackingCompass(plugin));
    }

    public TrackingCompass getTrackingCompass() {
        return items.get("TRACKING_COMPASS");
    }
}