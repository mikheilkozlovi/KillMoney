package me.rejomy.money.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class ColorUtil {

    public static String toColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
