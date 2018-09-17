package de.iani.cubeConomy.commands.money;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.iani.cubeConomy.CubeConomy;
import de.iani.cubeConomy.MoneyDatabaseException;
import de.iani.cubeConomy.commands.ArgsParser;
import de.iani.cubeConomy.commands.SubCommand;
import de.iani.playerUUIDCache.CachedPlayer;

public class MoneyCommand extends SubCommand {
    private CubeConomy plugin;

    public MoneyCommand(CubeConomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {
        String name = args.getNext(null);
        CachedPlayer player = plugin.getPlayerUUIDCache().getPlayerFromNameOrUUID(name == null ? sender.getName() : name);

        if (player == null) {
            sender.sendMessage(CubeConomy.MESSAGE_PREFIX + ChatColor.RED + "Unknown player");
            return true;
        }

        try {
            double money = plugin.getMoney(player.getUUID());
            if (name == null) {
                sender.sendMessage(CubeConomy.MESSAGE_PREFIX + "Balance: " + ChatColor.WHITE + plugin.formatMoney(money));
            } else {
                sender.sendMessage(CubeConomy.MESSAGE_PREFIX + player.getName() + "'s Balance: " + ChatColor.WHITE + plugin.formatMoney(money));
            }
        } catch (MoneyDatabaseException e) {
            sender.sendMessage(CubeConomy.MESSAGE_PREFIX + ChatColor.RED + "Database error: " + e.getMessage());
        }
        return true;
    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        if (args.remaining() == 1) {
            ArrayList<String> rv = new ArrayList<>();
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                rv.add(p.getName());
            }
            return rv;
        }
        return new ArrayList<>();
    }

}
