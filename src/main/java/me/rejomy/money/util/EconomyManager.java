package me.rejomy.money.util;

import me.rejomy.money.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class EconomyManager {
    private final Economy ECONOMY = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
    public EconomyManager() {
        if(ECONOMY == null) {
            Main.getInstance().getLogger().severe("");
            Main.getInstance().getLogger().severe("Vault not found!");
            Main.getInstance().getLogger().severe("");

            Bukkit.getPluginManager().disablePlugin(Main.getInstance());
        }
    }

    public boolean takeMoney(OfflinePlayer player, double price) {
        return ECONOMY.getBalance(player) >= price && ECONOMY.withdrawPlayer(player, price)
                .transactionSuccess();
    }

    public void giveMoney(OfflinePlayer player, double money) {
        ECONOMY.depositPlayer(player, money);
    }

    public double getBalance(OfflinePlayer player) {
        return ECONOMY.getBalance(player);
    }

}
