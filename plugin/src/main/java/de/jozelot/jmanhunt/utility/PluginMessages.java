package de.jozelot.jmanhunt.utility;

import de.jozelot.jmanhunt.JManhunt;
import org.bukkit.Bukkit;

public class PluginMessages {

    public static void sendStartup(JManhunt plugin) {
        var sender = Bukkit.getConsoleSender();
        var version = plugin.getDescription().getVersion();
        var mcVersion = Bukkit.getBukkitVersion();

        String prefix = "§8[§bJManhunt§8]";

        sender.sendMessage("");
        sender.sendMessage(prefix + "§7 Minecraft läuft in der §a" + mcVersion);
        sender.sendMessage(prefix + "§8 ----------------------------------------------");
        sender.sendMessage(prefix + "§b    +==================+");
        sender.sendMessage(prefix + "§b    |     JManhunt     |");
        sender.sendMessage(prefix + "§b    +==================+");
        sender.sendMessage(prefix + "§8 ----------------------------------------------");
        sender.sendMessage(prefix + "§7    Version: §a" +  version);
        sender.sendMessage(prefix + "§8 ----------------------------------------------");
        sender.sendMessage(prefix + "§a Plugin start was successful");
        sender.sendMessage("");
    }
}
