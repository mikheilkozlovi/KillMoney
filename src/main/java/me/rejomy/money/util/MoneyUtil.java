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

            if (Main.getInstance().getEconomyManager().takeMoney(offlinePlayer, Math.min(configEntity.getMax(), money))) {
                entity.sendMessage(ColorUtil.toColor(Config.INSTANCE.getMessageLoss()).replace("$money", String.valueOf(money)));
            }
        }

        return money;
    }

}
