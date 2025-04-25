package me.rejomy.money.util;

import lombok.experimental.UtilityClass;
import me.rejomy.money.Main;
import me.rejomy.money.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class DeathHandler {

    private List<EntityProfile> entityProfileList = new ArrayList<>();

    static {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(Main.getInstance(), () ->
                entityProfileList.removeIf(entityProfile -> {
                    if (entityProfile.getOwner() == null)
                        return true;

                    entityProfile.getDamageInfo().entrySet().removeIf(entry -> System.currentTimeMillis() - entry.getValue()[1] > Config.INSTANCE.getEqualExchangeKeepTime());

                    return entityProfile.getDamageInfo().isEmpty();
                }), 600, 600);
    }

    public void handle(Entity victim) {
        EntityProfile entityProfile = entityProfileList.stream().filter(eProfile -> eProfile.getOwner() == victim).findAny().orElse(null);

        // Maybe target does not receive any damage before, so that can be null.
        if (entityProfile != null) {
            handle(entityProfile);
        }
    }

    public void handle(EntityProfile entityProfile) {
        Entity victim = entityProfile.getOwner();
        ConfigEntity configEntity = Config.INSTANCE.getEntities().stream()
                .filter(target -> target.getEntity() == victim.getType())
                .findAny()
                .orElseGet(() -> Config.INSTANCE.getEntities().stream()
                        .filter(target -> target.getEntity() == null)
                        .findFirst()
                        .orElse(null));

        // There are no registered entity with this data or other section did not create.
        if (configEntity == null)
            return;

        Entity killer = entityProfile.getLastAttacker();
        long lastHitWasSince = System.currentTimeMillis() - entityProfile.getLastTakeDamageTime();

        // We cant handle this death case, because since last damage to this object passed more than limited time.
        if (lastHitWasSince > Config.INSTANCE.getMaxTimeInSecondsSinceLastHit() * 1000) {
            // If player die single scenario enabled, drop or take money.
            if (victim instanceof Player && Config.INSTANCE.isAllowPlayerDeathDrop() || Config.INSTANCE.isAllowMobDeathDrop()) {
                processMoneyTransfer(configEntity, victim, killer, entityProfile);
            }

            return;
        }

        boolean victimIsPlayer = victim instanceof Player;
        boolean killerIsNotAPlayer = !(killer instanceof Player);

        if (victimIsPlayer && !(killerIsNotAPlayer && !Config.INSTANCE.isAllowPlayerKillByEntityDrop()) ||
            !victimIsPlayer && killerIsNotAPlayer && Config.INSTANCE.isAllowEntityKillByEntityDrop()) {
            processMoneyTransfer(configEntity, killer, victim, entityProfile);

            if (Config.INSTANCE.isPlaySound()) {
                victim.getWorld().playSound(victim.getLocation(), Config.INSTANCE.getSound(), 2, 2);
            }

            // Reset map about damage.
            entityProfile.getDamageInfo().clear();
        }
    }

    public void damage(Entity attacker, Entity victim, double finalDamage) {
        if (// Check if world is ignored
                Config.INSTANCE.getIgnoredWorlds().contains(victim.getWorld().getName())
                        || // Check if victim doesn't living entity
                        !(victim instanceof LivingEntity)
                        || // Check if victim and attacker not Player, it means the mob killed a mob.
                        !(attacker instanceof Player) && !(victim instanceof Player) &&
                                !Config.INSTANCE.isAllowEntityKillByEntityDrop() && !Config.INSTANCE.isAllowPlayerKillByEntityDrop()
        ) return;

        boolean isDied = ((LivingEntity) victim).getHealth() <= finalDamage;
        EntityProfile entityProfile;

        entityProfile = entityProfileList.stream().filter(eProfile -> eProfile.getOwner() == victim).findAny().orElseGet(() -> {
            EntityProfile entityProfileTemp = new EntityProfile(victim);
            entityProfileList.add(entityProfileTemp);
            return entityProfileTemp;
        });

        entityProfile.addDamage(attacker, finalDamage);

        // Check if victim not death
        if (!isDied || Config.INSTANCE.isNotTriggerOnDamage())
            return;

        handle(entityProfile);
    }

    private void giveMoney(Player player, Entity victim, int money) {
        String message = Config.INSTANCE.getMessageReceive();
        PlayerUtil.sendMessage(player, message,
                "money", String.valueOf(money),
                "entity", victim.getName()
        );

        Main.getInstance().getEconomyManager().giveMoney(player, money);
    }


    private void processMoneyTransfer(ConfigEntity configEntity, Entity killer, Entity victim, EntityProfile entityProfile) {
        int money = MoneyUtil.getMoneyFromConfigAndTake(configEntity, victim);

        // We cant drop or take money smaller than or equals zero, so ignore these values.
        if (money <= 0)
            return;

        if (configEntity.getReceiveType() == ConfigEntity.ReceiveType.DROP) {
            PlayerUtil.drop(victim, victim.getLocation(), money);
        } else if (configEntity.getReceiveType() == ConfigEntity.ReceiveType.LAST_HIT) {
            if (killer instanceof Player) {
                giveMoney((Player) killer, victim, money);
            }
        } else if (configEntity.getReceiveType() == ConfigEntity.ReceiveType.EQUAL_EXCHANGE) {
            double damageSum = entityProfile.getDamageInfo().values().stream().mapToDouble(value -> value[0]).sum();

            int moneyPerOneDamage = (int) Math.floor(money / damageSum);

            entityProfile.getDamageInfo()
                    .forEach((entity, value) -> {
                        if (entity instanceof Player player)
                             giveMoney(player, victim, ((int) value[0]) * moneyPerOneDamage);
                    });
        }
    }
}
