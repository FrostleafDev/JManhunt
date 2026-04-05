package de.jozelot.jmanhunt.utility;

import de.jozelot.jmanhunt.JManhunt;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;
import java.util.logging.Level;

public class PluginMessages {

    private static final MiniMessage mm = MiniMessage.miniMessage();

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

    public static void sendHelpMessage(CommandSender sender, JManhunt plugin) {
        if (!sender.hasPermission("jmanhunt.admin")) {
            sender.sendMessage(mm.deserialize(String.join("<newline>", plugin.getBootstrap().getLangManager().formatList("command-jmanhunt-help-not-admin", null))));
            return;
        }
        sender.sendMessage(mm.deserialize(String.join("<newline>", plugin.getBootstrap().getLangManager().formatList("command-jmanhunt-help-admin", null))));
    }

    public static void sendWipeError(JManhunt plugin) {
        plugin.getLogger().log(Level.SEVERE, "");
        plugin.getLogger().log(Level.SEVERE, "------------------------------");
        plugin.getLogger().log(Level.SEVERE, " --- DO NOT REPORT THIS TO JMANHUNT - THIS IS NOT A BUG OR A CRASH ---");
        plugin.getLogger().log(Level.SEVERE, "");
        plugin.getLogger().log(Level.SEVERE, "You can savely ignore the following error!");
        plugin.getLogger().log(Level.SEVERE, "");
        plugin.getLogger().log(Level.SEVERE, " --- DO NOT REPORT THIS TO JMANHUNT - THIS IS NOT A BUG OR A CRASH ---");
        plugin.getLogger().log(Level.SEVERE, "------------------------------");
        plugin.getLogger().log(Level.SEVERE, "");
    }
}
