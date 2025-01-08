package me.rejomy.money;

import lombok.Getter;
import me.rejomy.money.config.Config;
import me.rejomy.money.listener.DamageListener;
import me.rejomy.money.listener.PickupListener;
import me.rejomy.money.util.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Main extends JavaPlugin {

    public static Main INSTANCE;

    public static Main getInstance() {
        return INSTANCE;
    }


    private EconomyManager economyManager;

    @Override
    public void onEnable() {
        INSTANCE = this;

        economyManager = new EconomyManager();

        Config.INSTANCE.load();

        Bukkit.getPluginManager().registerEvents(new PickupListener(), this);
        Bukkit.getPluginManager().registerEvents(new DamageListener(), this);

        saveDefaultConfig();
    }

}