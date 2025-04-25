package me.rejomy.money.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.EntityType;

@Getter
@Setter
public class ConfigEntity {
    EntityType entity;
    ReceiveType receiveType;
    // Static money value, if necessary.
    int money = -1;
    // Money value in percentage of total entity balance
    // can be adjusted by max if necessary.
    int percent;
    // Max amount of money that entity able to lose.
    int maxMoney = Integer.MAX_VALUE;

    public ConfigEntity() {}

    public enum ReceiveType {
        EQUAL_EXCHANGE,
        LAST_HIT,
        DROP
    }
}
