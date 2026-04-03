package de.jozelot.jmanhunt.core;

import de.jozelot.jmanhunt.JManhunt;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;

public class JManhuntBootstrapImpl implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        File resetFlag = new File("reset_worlds.txt");

        if (resetFlag.exists()) {
            context.getLogger().info("Reset flag found. Deleting worlds...");

            deleteWorldFolder(new File("world"));
            deleteWorldFolder(new File("world_nether"));
            deleteWorldFolder(new File("world_the_end"));

            resetFlag.delete();
            context.getLogger().info("World deleting successful");
        }
    }

    private void deleteWorldFolder(File path) {
        if (!path.exists()) return;
        try {
            Files.walk(path.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(java.nio.file.Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            System.err.println("Erro wile deleting " + path.getName() + ": " + e.getMessage());
        }
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new JManhunt();
    }
}