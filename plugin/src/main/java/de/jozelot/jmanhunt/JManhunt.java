package de.jozelot.jmanhunt;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class JManhunt extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "Plugin is starting...");


    }

    @Override
    public void onDisable() {

    }
}
