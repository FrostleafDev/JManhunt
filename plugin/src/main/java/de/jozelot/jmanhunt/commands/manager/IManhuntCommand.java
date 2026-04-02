package de.jozelot.jmanhunt.commands.manager;

import java.util.Collection;

public interface IManhuntCommand {

    void register(Collection<String> names);
}
