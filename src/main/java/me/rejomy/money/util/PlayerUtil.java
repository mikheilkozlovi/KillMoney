package me.rejomy.money.util;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class PlayerUtil {

    public void sendMessage(Player player, String message, String... placeholders) {
        message = ColorUtil.toColor(message);
        message = StringUtil.format(message, placeholders);

        if (!message.isEmpty())
            player.sendMessage(message);
    }
}
