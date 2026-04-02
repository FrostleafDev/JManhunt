package de.jozelot.jmanhunt.registry;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.commands.ManhuntCommand;

public class CommandRegistry {

    private final JManhunt plugin;

    public CommandRegistry(JManhunt plugin) {
        this.plugin = plugin;
    }

    /**
     * All Bukkit Commands are registered here
     */
    public void register() {
        new ManhuntCommand(plugin).register();
    }
}
