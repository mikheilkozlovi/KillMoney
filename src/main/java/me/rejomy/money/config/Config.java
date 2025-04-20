package me.rejomy.money.config;

import lombok.Getter;
import me.rejomy.money.Main;
import me.rejomy.money.util.ConfigEntity;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public enum Config {
    INSTANCE;

    List<String> ignoredWorlds;

    int equalExchangeKeepTime;

    String messagePickup;
    String messageReceive;
    String messageLoss;

    final HashMap<Integer, Material> dropMaterials = new HashMap<>();

    boolean dropByDefault;
    boolean playSound;

    Sound sound;

    final List<ConfigEntity> entities = new ArrayList<>();

    public void load() {
        FileConfiguration config = Main.getInstance().getConfig();

        this.ignoredWorlds = config.getStringList("ignore-world");

        this.dropByDefault = config.getBoolean("drop-by-default");
        this.playSound = config.getBoolean("sound.enable");
        // Check the sound validate.
        String soundName = config.getString("sound.sound");
        this.playSound = soundIsValid(soundName);

        if (this.playSound) {
            sound = Sound.valueOf(soundName);
        }
        //

        this.messagePickup = config.getString("message.pickup");
        this.messageReceive = config.getString("message.give");
        this.messageLoss = config.getString("message.loss");

        this.equalExchangeKeepTime = config.getInt("equal-exchange.keep-time");

        for (String amount : config.getConfigurationSection("drop-material").getKeys(false)) {
            if (!canParseInt(amount)) continue;
            String material = config.getString("drop-material." + amount);
            if (!materialIsValid(material)) continue;

            dropMaterials.put(Integer.parseInt(amount), Material.valueOf(material));
        }

        for (String entity : config.getConfigurationSection("entities").getKeys(false)) {
            boolean isNotOther = !entity.equalsIgnoreCase("other");

            if (isNotOther && !entityIsValid(entity))
                continue;

            String path = "entities." + entity + ".";
            String money = config.getString(path + "money");
            ConfigEntity configEntity = new ConfigEntity();

            // Set EntityType only if it is not other section, in other we will run actions for non recognized entities.
            if (isNotOther)
                configEntity.setEntity(EntityType.valueOf(entity));

            if ((dropByDefault && config.get(path + "drop") == null) || config.getBoolean(path + "drop")) {
                configEntity.setReceiveType(ConfigEntity.ReceiveType.DROP);
            } else {
                if (config.getBoolean(path + "equal-exchange")) {
                    configEntity.setReceiveType(ConfigEntity.ReceiveType.EQUAL_EXCHANGE);
                }
                else configEntity.setReceiveType(ConfigEntity.ReceiveType.LAST_HIT);
            }

            if (money.contains("%")) {
                money = money.replace("%", "");

                if (canParseInt(money)) {
                    configEntity.setPercent(Integer.parseInt(money));
                }
            } else if (canParseInt(money)) {
                configEntity.setMoney(Integer.parseInt(money));
            }

            if (config.get(path + "max-money") != null) {
                configEntity.setMax(config.getInt(path + "max-money"));
            }

            entities.add(configEntity);
        }
    }

    private boolean soundIsValid(String value) {
        try {
            Sound.valueOf(value);
            return true;
        } catch (IllegalArgumentException exception) {
            Main.getInstance().getLogger().warning("Sound: " + value + " not found!");
            Main.getInstance().getLogger().warning("Disabling sound system...");
            return false;
        }
    }

    private boolean entityIsValid(String value) {
        try {
            EntityType.valueOf(value);
            return true;
        } catch (IllegalArgumentException exception) {
            Main.getInstance().getLogger().warning("Entity: " + value + " not found!");
            Main.getInstance().getLogger().warning("We skip the entity.");
            return false;
        }
    }

    private boolean materialIsValid(String value) {
        try {
            Material.valueOf(value);
            return true;
        } catch (IllegalArgumentException exception) {
            Main.getInstance().getLogger().warning("Material: " + value + " not found!");
            Main.getInstance().getLogger().warning("We skip this value.");
            return false;
        }
    }

    private boolean canParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException exception) {
            Main.getInstance().getLogger().warning("Money: " + value + " is not a number!");
            Main.getInstance().getLogger().warning("We skip this value");
            return false;
        }
    }

    public Material getDropMaterial(int money) {
        return dropMaterials.entrySet().stream()
                .filter(entry -> entry.getKey() >= money)
                .min(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .orElse(Material.DIAMOND);
    }
}
