package me.rejomy.money.util;

import lombok.Getter;
import me.rejomy.money.config.Config;
import org.bukkit.entity.Entity;

import java.util.HashMap;

@Getter
public class EntityProfile {

    HashMap<Entity, double[]> damageInfo = new HashMap<>();
    private final Entity owner;
    private Entity lastAttacker;
    private long lastTakeDamageTime;

    public EntityProfile(Entity owner) {
        this.owner = owner;
    }

    public void addDamage(Entity attacker, double damage) {
        // Set variables for easily tracking last damage time and attacker.
        lastTakeDamageTime = System.currentTimeMillis();
        lastAttacker = attacker;

        double[] data = damageInfo.getOrDefault(attacker, new double[]{0, System.currentTimeMillis()});

        if (System.currentTimeMillis() - data[1] > Config.INSTANCE.getEqualExchangeKeepTime()) {
            data[0] = 0;
        }

        data[0] += damage;
        data[1] = System.currentTimeMillis();

        damageInfo.put(attacker, data);
    }
}
