package me.rejomy.money.util;

import lombok.experimental.UtilityClass;
import me.rejomy.money.config.Config;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PlayerUtil {

    public void sendMessage(Player player, String message, String... placeholders) {
        message = ColorUtil.toColor(message);
        message = StringUtil.format(message, placeholders);

        if (!message.isEmpty())
            player.sendMessage(message);
    }

    void drop(Entity victim, Location location, int money) {
        ItemStack dropItem = new ItemStack(Config.INSTANCE.getDropMaterial(money), 1);
        ItemMeta meta = dropItem.getItemMeta();

        List<String> lore = new ArrayList<>();

        lore.add("$" + money);
        lore.add(victim.getName());

        meta.setLore(lore);

        dropItem.setItemMeta(meta);

        Item item = location.getWorld().dropItemNaturally(location, dropItem);
        String title = Config.INSTANCE.getDropTitle();
        title = ColorUtil.toColor(title);
        title = StringUtil.format(title, "amount", String.valueOf(money));

        item.setCustomName(title);
        item.setCustomNameVisible(true);
    }
}
