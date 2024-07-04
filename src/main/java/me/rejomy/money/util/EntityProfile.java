package me.rejomy.money.util;

import lombok.Getter;
import me.rejomy.money.config.Config;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class EntityProfile {
    public Entity owner;

    public EntityProfile(Entity owner) {
        this.owner = owner;
    }

    @Getter
    HashMap<Player, double[]> damageInfo = new HashMap<>();

    public void addDamage(Player player, double damage) {
        double[] data = damageInfo.getOrDefault(player, new double[] {0, System.currentTimeMillis()});

        if (System.currentTimeMillis() - data[1] > Config.INSTANCE.getEqualExchangeKeepTime()) {
            data[0] = 0;
        }

        data[0] += damage;
        data[1] = System.currentTimeMillis();

        damageInfo.put(player, data);
    }
}
