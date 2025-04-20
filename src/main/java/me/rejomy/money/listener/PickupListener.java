package me.rejomy.money.listener;

import me.rejomy.money.Main;
import me.rejomy.money.config.Config;
import me.rejomy.money.util.ColorUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PickupListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();

        ItemStack item = event.getItem().getItemStack();
        ItemMeta meta = item.getItemMeta();

        if (!item.hasItemMeta() || !meta.hasLore() || meta.getLore().size() != 1)
            return;

        String itemLore = meta.getLore().get(0);

        if (!itemLore.contains("$"))
            return;

        try {
            int price = Integer.parseInt(itemLore.replace("$", ""));

            if (entity instanceof Player) {
                String message = Config.INSTANCE.getMessagePickup();

                if (!message.isEmpty()) {
                    entity.sendMessage(ColorUtil.toColor(message.replace("$money", String.valueOf(price))));
                }

                Main.getInstance().getEconomyManager().giveMoney((OfflinePlayer) entity, price);

                event.getItem().remove();
            }

            event.setCancelled(true);

        } catch (NumberFormatException exception) {
        }

    }

}
