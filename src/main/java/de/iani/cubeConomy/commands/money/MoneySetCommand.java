package de.iani.cubeConomy.commands.money;

import de.iani.cubeConomy.CubeConomy;
import de.iani.cubeConomy.Messages;
import de.iani.cubeConomy.MoneyDatabaseException;
import de.iani.cubeConomy.Permissions;
import de.iani.cubeConomy.commands.ArgsParser;
import de.iani.cubeConomy.commands.SubCommand;
import de.iani.cubeConomy.events.Cause;
import de.iani.playerUUIDCache.CachedPlayer;
import java.util.ArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        return "<name> <amount> [reason]";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {
        String name = args.getNext(null);

        if (!args.hasNext()) {
            sender.sendMessage(Component.text(commandString + getUsage()));
            return true;
        }
        double amount = args.getNext(0.0);
        if (Double.isInfinite(amount) || Double.isNaN(amount)) {
            sender.sendMessage(Messages.error("Invalid amount"));
            return true;
        }

        CachedPlayer player = plugin.getPlayerUUIDCache().getPlayerFromNameOrUUID(name);

        if (player == null) {
            sender.sendMessage(Messages.error("Unknown player"));
            return true;
        }

        String reason = args.getAll(null);

        try {
            double oldamount = plugin.getMoney(player.getUUID());
            plugin.setMoney(sender, player.getUUID(), amount, Cause.SET_COMMAND, reason);
            plugin.getLogger().info(sender.getName() + " has set the money for " + player.getName() + " to " + plugin.formatMoney(amount) + (reason == null ? "" : " with reason \"" + reason + "\"") + ". Old amount: " + plugin.formatMoney(oldamount));

            Component reasonMessage = Messages.reason(reason, NamedTextColor.DARK_GREEN);
            sender.sendMessage(Messages.prefixed(Component.text(player.getName() + "'s balance has been changed to ", NamedTextColor.DARK_GREEN)
                    .append(Component.text(plugin.formatMoney(amount), NamedTextColor.WHITE))
                    .append(reasonMessage)
                    .append(Component.text(".", NamedTextColor.DARK_GREEN))));
            if (sender instanceof Player) {
                plugin.sendMessageTo((Player) sender, player.getUUID(), Messages.prefixed(Component.text(sender.getName(), NamedTextColor.WHITE)
                        .append(Component.text(" has set your money to ", NamedTextColor.DARK_GREEN))
                        .append(Component.text(plugin.formatMoney(amount), NamedTextColor.WHITE))
                        .append(reasonMessage)
                        .append(Component.text(".", NamedTextColor.DARK_GREEN))));
            }
        } catch (MoneyDatabaseException e) {
            sender.sendMessage(Messages.error("Database error: " + e.getMessage()));
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
