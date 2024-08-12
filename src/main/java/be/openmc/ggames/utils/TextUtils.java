package be.openmc.ggames.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.Arrays;

public class TextUtils {
    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static boolean checkInvFull(Player player) {
        return !Arrays.asList(player.getInventory().getStorageContents()).contains(null);
    }
}
