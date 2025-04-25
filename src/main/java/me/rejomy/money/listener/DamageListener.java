package me.rejomy.money.listener;

import me.rejomy.money.util.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity
                attacker = event.getDamager(),
                victim = event.getEntity();

        DeathHandler.damage(attacker, victim, event.getFinalDamage());
    }
}
