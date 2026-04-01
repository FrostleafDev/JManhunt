package de.jozelot.jmanhunt;

import de.jozelot.jmanhunt.core.JManhuntBootstrap;
import de.jozelot.jmanhunt.utility.PluginMessages;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;

public final class JManhunt extends JavaPlugin {

    private JManhuntBootstrap bootstrap;

    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "Plugin is starting...");

        bootstrap = new JManhuntBootstrap(this);

        // Maybe there will be a dependency check
        bootstrap.initialize();

        if (!bootstrap.enable()) {
            Bukkit.getPluginManager().disablePlugin(this);
            getLogger().log(Level.SEVERE, "Plugin start failed. For more information look above");
            return;
        }

        PluginMessages.sendStartup(this);
    }

    @Override
    public void onDisable() {
        bootstrap.shutdown();
    }

    public JManhuntBootstrap getBootstrap() {
        return bootstrap;
    }
}
