package de.iani.cubeConomy.commands.money;

import de.iani.cubeConomy.CubeConomy;
import de.iani.cubeConomy.MoneyDatabaseException;
import de.iani.cubeConomy.MoneyException;
import de.iani.cubeConomy.commands.ArgsParser;
import de.iani.cubeConomy.commands.SubCommand;
import de.iani.cubeConomy.events.Cause;
import de.iani.playerUUIDCache.CachedPlayer;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyPayCommand extends SubCommand {
    private CubeConomy plugin;

    public MoneyPayCommand(CubeConomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "<name> <amount> [reason]";
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {
        String name = args.getNext(null);
        Player senderPlayer = (Player) sender;
        if (!args.hasNext()) {
            sender.sendMessage(commandString + getUsage());
            return true;
        }
        double amount = args.getNext(0.0);
        amount = (Math.round(amount * 100)) / 100.0;
        if (!Double.isFinite(amount) || amount < 0.01) {
            sender.sendMessage(CubeConomy.MESSAGE_PREFIX + ChatColor.RED + "Amount must be positive");
            return true;
        }

        CachedPlayer player = plugin.getPlayerUUIDCache().getPlayerFromNameOrUUID(name);

        if (player == null) {
            sender.sendMessage(CubeConomy.MESSAGE_PREFIX + ChatColor.RED + "Unknown player");
            return true;
        }

        if (senderPlayer.getUniqueId().equals(player.getUUID())) {
            sender.sendMessage(CubeConomy.MESSAGE_PREFIX + ChatColor.RED + "Cannot send money to yourself");
            return true;
        }

        StringBuilder reason = new StringBuilder(args.getNext(""));
        while (args.hasNext()) {
            reason.append(' ').append(args.getNext(""));
        }

        try {
            String reasonMessage = reason.toString().equals("") ? "" : " for " + ChatColor.WHITE + reason + ChatColor.DARK_GREEN;
            plugin.transferMoney(sender, senderPlayer.getUniqueId(), player.getUUID(), amount, Cause.PAY_COMMAND, reason.toString());
            plugin.getLogger().info(sender.getName() + " has sent " + plugin.formatMoney(amount) + " to " + player.getName() + (reason.toString().equals("") ? "" : " with reason \"" + reason + "\""));
            sender.sendMessage(CubeConomy.MESSAGE_PREFIX + "You have sent " + ChatColor.WHITE + plugin.formatMoney(amount) + ChatColor.DARK_GREEN + " to " + ChatColor.WHITE + player.getName() + ChatColor.DARK_GREEN + reasonMessage + ".");
            plugin.sendMessageTo(senderPlayer, player.getUUID(), CubeConomy.MESSAGE_PREFIX + ChatColor.WHITE + sender.getName() + ChatColor.DARK_GREEN + " has sent to you " + ChatColor.WHITE + plugin.formatMoney(amount) + ChatColor.DARK_GREEN + reasonMessage + ".");
        } catch (MoneyDatabaseException e) {
            sender.sendMessage(CubeConomy.MESSAGE_PREFIX + ChatColor.RED + "Database error: " + e.getMessage());
        } catch (MoneyException e) {
            sender.sendMessage(CubeConomy.MESSAGE_PREFIX + ChatColor.RED + e.getMessage());
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
