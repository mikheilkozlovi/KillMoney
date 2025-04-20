package me.rejomy.money.command;

import me.rejomy.money.Main;
import me.rejomy.money.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class KillMoneyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
        } else {
            if (args[0].equalsIgnoreCase("reload")) {
                Main.getInstance().saveDefaultConfig();
                Config.INSTANCE.load();
                sender.sendMessage(ChatColor.GREEN + "[KillMoney] Plugin has been reloaded.");
            } else {
                sendHelp(sender);
            }
        }

        return false;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + " Invalid syntax, please use " + ChatColor.GOLD + "/killmoney reload");
    }
}
