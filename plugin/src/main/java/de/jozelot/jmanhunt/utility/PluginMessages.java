package de.jozelot.jmanhunt.utility;

import de.jozelot.jmanhunt.JManhunt;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.UUID;
import java.util.logging.Level;

public class PluginMessages {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final String prefix = "<dark_gray>[<aqua>JManhunt<dark_gray>]";

    public static void sendStartup(JManhunt plugin) {
        var sender = Bukkit.getConsoleSender();
        var version = plugin.getDescription().getVersion();
        var mcVersion = Bukkit.getBukkitVersion();

        sender.sendMessage("");
        sender.sendMessage(mm.deserialize(prefix + "<gray> Minecraft läuft in der <green>" + mcVersion));
        sender.sendMessage(mm.deserialize(prefix + "<dark_gray> ----------------------------------------------"));
        sender.sendMessage(mm.deserialize(prefix + "<aqua>    +==================+"));
        sender.sendMessage(mm.deserialize(prefix + "<aqua>    |     JManhunt     |"));
        sender.sendMessage(mm.deserialize(prefix + "<aqua>    +==================+"));
        sender.sendMessage(mm.deserialize(prefix + "<dark_gray> ----------------------------------------------"));
        sender.sendMessage(mm.deserialize(prefix + "<gray>    Version: <green>" +  version));
        sender.sendMessage(mm.deserialize(prefix + "<dark_gray> ----------------------------------------------"));
        sender.sendMessage(mm.deserialize(prefix + "<green> Plugin start was successful"));
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

    public static void sendDebugWarning() {
        var sender = Bukkit.getConsoleSender();

        sender.sendMessage("");
        sender.sendMessage(mm.deserialize(prefix + "<dark_gray>>----------------------------------------------" ));
        sender.sendMessage(mm.deserialize(prefix + "<gray>"));
        sender.sendMessage(mm.deserialize(prefix + "<gray>                <red><b>!!! DEBUG MODE ACTIVE !!!</b></red>" ));
        sender.sendMessage(mm.deserialize(prefix + "<gray>" ));
        sender.sendMessage(mm.deserialize(prefix + "<gray>  The plugin is running in development mode.</gray>" ));
        sender.sendMessage(mm.deserialize(prefix + "<gray>  - Permissions are bypassed for debug commands.</gray>" ));
        sender.sendMessage(mm.deserialize(prefix + "<gray>  - Internal data is logged to the console.</gray>" ));
        sender.sendMessage(mm.deserialize(prefix + "<gray>"));
        sender.sendMessage(mm.deserialize(prefix + "<gray>        <red><b>DO NOT USE THIS ON A PRODUCTION SERVER!</b></red>" ));
        sender.sendMessage(mm.deserialize(prefix + "<gray>"));
        sender.sendMessage(mm.deserialize(prefix + "<dark_gray>>----------------------------------------------" ));
        sender.sendMessage("");
    }
}
