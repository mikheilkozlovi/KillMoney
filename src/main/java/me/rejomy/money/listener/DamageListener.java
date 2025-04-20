package me.rejomy.money.listener;

import me.rejomy.money.Main;
import me.rejomy.money.config.Config;
import me.rejomy.money.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DamageListener implements Listener {

    List<EntityProfile> entityProfileList = new ArrayList<>();

    public DamageListener() {
        if (Config.INSTANCE.getEntities().stream()
                .anyMatch(configEntity -> configEntity.getReceiveType() == ConfigEntity.ReceiveType.EQUAL_EXCHANGE)) {
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(Main.getInstance(), () ->
                    entityProfileList.removeIf(entityProfile -> {
                        if (entityProfile.owner == null)
                            return true;

                        entityProfile.getDamageInfo().entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue()[1] > Config.INSTANCE.getEqualExchangeKeepTime());

                        return entityProfile.getDamageInfo().isEmpty();
                    }), 600, 600);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity
                killer = event.getDamager(),
                victim = event.getEntity();

        if (// Check if world is ignored
                Config.INSTANCE.getIgnoredWorlds().contains(victim.getWorld().getName())
                        || // Check if victim doesn't living entity
                        !(victim instanceof LivingEntity)
                        || // Check if victim and killer not Player, it means the mob killed a mob.
                        !(killer instanceof Player) && !(victim instanceof Player)) return;

        boolean isDied = ((LivingEntity) victim).getHealth() <= event.getFinalDamage();

        ConfigEntity configEntity = Config.INSTANCE.getEntities().stream()
                .filter(target -> target.getEntity() == victim.getType())
                .findAny()
                .orElseGet(() -> Config.INSTANCE.getEntities().stream()
                        .filter(target -> target.getEntity() == null)
                        .findFirst()
                        .orElse(null));


        if (configEntity == null)
            return;

        EntityProfile entityProfile = null;

        if (killer instanceof Player && configEntity.getReceiveType() == ConfigEntity.ReceiveType.EQUAL_EXCHANGE) {
            entityProfile = entityProfileList.stream().filter(eProfile -> eProfile.owner == victim).findAny().orElse(null);

            if (entityProfile == null) {
                entityProfile = new EntityProfile(victim);
            }

            entityProfile.addDamage((Player) killer, event.getFinalDamage());
        }

        // Check if victim not death
        if (!isDied)
            return;

        int money = MoneyUtil.getMoneyFromConfig(configEntity, victim);

        if (money <= 0)
            return;

        if (configEntity.getReceiveType() == ConfigEntity.ReceiveType.DROP) {
            drop(victim.getLocation(), money);
        } else if (configEntity.getReceiveType() == ConfigEntity.ReceiveType.LAST_HIT) {
            if (killer instanceof Player) {
                giveMoney(((Player) killer).getPlayer(), money);
            }
        } else if (entityProfile != null && configEntity.getReceiveType() == ConfigEntity.ReceiveType.EQUAL_EXCHANGE) {
            double damageSum = entityProfile.getDamageInfo().values().stream().mapToDouble(value -> value[0]).sum();

            int moneyPerOneDamage = (int) Math.floor(money / damageSum);

            entityProfile.getDamageInfo()
                    .forEach((key, value) -> giveMoney(key, ((int) value[0]) * moneyPerOneDamage));

            entityProfileList.remove(entityProfile);
        }

        if (Config.INSTANCE.isPlaySound()) {
            victim.getWorld().playSound(victim.getLocation(), Config.INSTANCE.getSound(), 2, 2);
        }
    }

    private void giveMoney(Player player, int money) {
        String message = Main.getInstance().getConfig().getString("message.pickup");

        if (!message.isEmpty()) {
            player.sendMessage(ColorUtil.toColor(message.replace("$money", String.valueOf(money))));
        }

        Main.getInstance().getEconomyManager().giveMoney(player, money);
    }

    private void drop(Location location, int money) {
        ItemStack dropItem = new ItemStack(Config.INSTANCE.getDropMaterial(money), 1);
        ItemMeta meta = dropItem.getItemMeta();

        List<String> lore = new ArrayList<>();

        lore.add("$" + money);

        meta.setLore(lore);

        dropItem.setItemMeta(meta);

        Item item = location.getWorld().dropItemNaturally(location, dropItem);

        item.setCustomName(ColorUtil.toColor("&6&l" + money));
        item.setCustomNameVisible(true);
    }
}
