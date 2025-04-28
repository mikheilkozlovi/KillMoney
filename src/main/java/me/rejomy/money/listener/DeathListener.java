package me.rejomy.money.listener;

import me.rejomy.money.config.Config;
import me.rejomy.money.util.DeathHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DeathListener implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (Config.INSTANCE.isNotTriggerOnDamage()) {
            DeathHandler.handle(event.getEntity());
        }
    }
}
