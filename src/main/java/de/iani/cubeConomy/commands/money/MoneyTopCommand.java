package de.iani.cubeConomy.commands.money;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.iani.cubeConomy.CubeConomy;
import de.iani.cubeConomy.commands.ArgsParser;
import de.iani.cubeConomy.commands.SubCommand;
import de.iani.playerUUIDCache.CachedPlayer;

public class MoneyTopCommand extends SubCommand {
    private CubeConomy plugin;

    public MoneyTopCommand(CubeConomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "[count]";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {
        int count = args.getNext(5);
        if (count < 0 || count > 20) {
            count = 5;
        }

        LinkedHashMap<UUID, Double> accounts;
        try {
            accounts = plugin.getPluginDatabase().listTop(0, count);
        } catch (SQLException e1) {
            sender.sendMessage(CubeConomy.MESSAGE_PREFIX + ChatColor.RED + "Database error");
            return true;
        }
        sender.sendMessage(ChatColor.DARK_GREEN + "-----[ " + ChatColor.WHITE + "Wealthiest Accounts " + ChatColor.DARK_GREEN + "]-----");
        int nr = 1;
        for (Entry<UUID, Double> e : accounts.entrySet()) {
            CachedPlayer p = plugin.getPlayerUUIDCache().getPlayer(e.getKey(), true);
            sender.sendMessage(ChatColor.DARK_GRAY.toString() + (nr++) + ". " + ChatColor.DARK_GREEN + (p != null ? p.getName() : e.getKey()) + ChatColor.DARK_GRAY + " - " + ChatColor.WHITE
                    + plugin.formatMoney(e.getValue()));
        }

        return true;
    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        return new ArrayList<>();
    }

}
