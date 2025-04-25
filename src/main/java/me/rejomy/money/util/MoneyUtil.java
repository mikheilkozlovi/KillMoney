package me.rejomy.money.util;

import me.rejomy.money.Main;
import me.rejomy.money.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class MoneyUtil {

    public static int getMoneyFromConfigAndTake(ConfigEntity configEntity, Entity entity) {
        int money = configEntity.getMoney();

        if (configEntity.getEntity() == EntityType.PLAYER) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entity.getName());

            if (configEntity.getPercent() > 0) {
                int balance = (int) Math.round(Main.getInstance().getEconomyManager().getBalance(offlinePlayer));

                money = (balance * configEntity.getPercent()) / 100;
            }

            // Limit the money amount between current and max.
            money = Math.min(configEntity.getMaxMoney(), money);

            if (Main.getInstance().getEconomyManager().takeMoney(offlinePlayer, money)) {
                String message = Config.INSTANCE.getMessageLoss();

                if (!message.isEmpty())
                    entity.sendMessage(ColorUtil.toColor(message).replace("$money", String.valueOf(money)));
            }
        }

        return money;
    }

}
