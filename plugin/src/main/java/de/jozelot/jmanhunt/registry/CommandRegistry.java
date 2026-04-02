package de.jozelot.jmanhunt.registry;

import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.commands.ManhuntCommand;
import de.jozelot.jmanhunt.commands.manager.CommandManager;

import java.util.List;

public class CommandRegistry {

    private final JManhunt plugin;
    private CommandManager commandManager;

    public CommandRegistry(JManhunt plugin) {
        this.plugin = plugin;
    }

    /**
     * All Bukkit Commands are registered here
     */
    public void register() {
        this.commandManager = new CommandManager();
        commandManager.registerCommands(new ManhuntCommand(plugin), List.of("jmanhunt", "manhunt", "jm"));
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
