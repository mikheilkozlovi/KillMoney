package me.rejomy.money.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.EntityType;

@Getter
@Setter
public class ConfigEntity {
    EntityType entity;
    ReceiveType receiveType;
    int money = -1;
    int percent;
    int max = Integer.MAX_VALUE;

    public ConfigEntity() {}

    public enum ReceiveType {
        EQUAL_EXCHANGE,
        LAST_HIT,
        DROP
    }
}
