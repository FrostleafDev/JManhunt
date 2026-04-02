package de.jozelot.jmanhunt.commands.manager;

import java.util.Collection;

public class CommandManager {

    public void registerCommands(IManhuntCommand commandExecutor, Collection<String> names) {
        commandExecutor.register(names);
    }
}
