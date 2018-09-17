package de.iani.cubeConomy.commands.money;

import java.util.ArrayList;

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

public class MoneySetCommand extends SubCommand {
    private CubeConomy plugin;

    public MoneySetCommand(CubeConomy plugin) {
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
        if (Double.isInfinite(amount) || Double.isNaN(amount)) {
            sender.sendMessage(CubeConomy.MESSAGE_PREFIX + ChatColor.RED + "Invalid amount");
            return true;
        }

        CachedPlayer player = plugin.getPlayerUUIDCache().getPlayerFromNameOrUUID(name);

        if (player == null) {
            sender.sendMessage(CubeConomy.MESSAGE_PREFIX + ChatColor.RED + "Unknown player");
            return true;
        }

        try {
            double oldamount = plugin.getMoney(player.getUUID());
            plugin.setMoney(player.getUUID(), amount);
            plugin.getLogger().info(sender.getName() + " has set the money for " + player.getName() + " to " + plugin.formatMoney(amount) + ". Old amount: " + plugin.formatMoney(oldamount));
            sender.sendMessage(CubeConomy.MESSAGE_PREFIX + player.getName() + "'s balance has been changed to: " + ChatColor.WHITE + plugin.formatMoney(amount));

            if (sender instanceof Player) {
                plugin.sendMessageTo((Player) sender, player.getUUID(), CubeConomy.MESSAGE_PREFIX + ChatColor.WHITE + sender.getName() + ChatColor.DARK_GREEN + " has set your money to " + ChatColor.WHITE + plugin.formatMoney(amount) + ChatColor.DARK_GREEN + ".");
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
