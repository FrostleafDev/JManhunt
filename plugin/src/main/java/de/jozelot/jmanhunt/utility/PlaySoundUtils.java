package de.jozelot.jmanhunt.utility;

import com.mojang.brigadier.Command;
import de.jozelot.jmanhunt.JManhunt;
import de.jozelot.jmanhunt.api.player.ManhuntPlayer;
import de.jozelot.jmanhunt.api.player.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlaySoundUtils {

    public static boolean playSound(CommandSender sender, Sound sound, JManhunt plugin) {
        if (!(sender instanceof Player player)) {
            return false;
        }
        ManhuntPlayer manhuntPlayer = plugin.getBootstrap().getManhuntPlayerManager().getPlayer(player);

        if (manhuntPlayer == null) {
            return false;
        }

        manhuntPlayer.playSound(sound);
        return true;
    }

    public static boolean playError(CommandSender sender, JManhunt plugin) {
        return playSound(sender, Sound.ERROR, plugin);
    }

    public static boolean playPling(CommandSender sender, JManhunt plugin) {
        return playSound(sender, Sound.PLING, plugin);
    }

    public static boolean playSuccess(CommandSender sender, JManhunt plugin) {
        return playSound(sender, Sound.SUCCESS, plugin);
    }

    public static boolean playWarning(CommandSender sender, JManhunt plugin) {
        return playSound(sender, Sound.WARNING, plugin);
    }

    public static boolean playExperience(CommandSender sender, JManhunt plugin) {
        return playSound(sender, Sound.EXPERIENCE, plugin);
    }


}
