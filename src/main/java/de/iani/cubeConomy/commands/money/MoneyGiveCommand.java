package de.iani.cubeConomy.commands.money;

import java.util.ArrayList;

import de.iani.cubeConomy.events.MoneyChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.iani.cubeConomy.CubeConomy;
import de.iani.cubeConomy.MoneyDatabaseException;
import de.iani.cubeConomy.Permissions;
import de.iani.cubeConomy.commands.ArgsParser;
import de.iani.cubeConomy.commands.SubCommand;
import de.iani.playerUUIDCache.CachedPlayer;

public class MoneyGiveCommand extends SubCommand {
    private CubeConomy plugin;

    public MoneyGiveCommand(CubeConomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getRequiredPermission() {
        return Permissions.CUBECONOMY_ADMIN;
    }

    @Override
    public String getUsage() {
        return "<name> <amount>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {
        String name = args.getNext(null);

        if (!args.hasNext()) {
            sender.sendMessage(commandString + getUsage());
            return true;
        }
        double amount = args.getNext(0.0);
        if (Double.isInfinite(amount) || Double.isNaN(amount) || amount <= 0) {
            sender.sendMessage(CubeConomy.MESSAGE_PREFIX + ChatColor.RED + "Invalid amount");
            return true;
        }

        CachedPlayer player = plugin.getPlayerUUIDCache().getPlayerFromNameOrUUID(name);

        if (player == null) {
            sender.sendMessage(CubeConomy.MESSAGE_PREFIX + ChatColor.RED + "Unknown player");
            return true;
        }

        try {
            plugin.changeMoney(player.getUUID(), amount);
            Bukkit.getPluginManager().callEvent(new MoneyChangeEvent(player.getUniqueId(), amount, System.currentTimeMillis()));

            plugin.getLogger().info(sender.getName() + " has given " + plugin.formatMoney(amount) + " to " + player.getName());
            sender.sendMessage(CubeConomy.MESSAGE_PREFIX + player.getName() + "'s account had " + ChatColor.WHITE + plugin.formatMoney(amount) + ChatColor.DARK_GREEN + " credited.");

            if (sender instanceof Player) {
                plugin.sendMessageTo((Player) sender, player.getUUID(), CubeConomy.MESSAGE_PREFIX + ChatColor.WHITE + sender.getName() + ChatColor.DARK_GREEN + " has given to you " + ChatColor.WHITE + plugin.formatMoney(amount) + ChatColor.DARK_GREEN + ".");
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
