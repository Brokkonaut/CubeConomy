package de.iani.cubeConomy.commands.money;

import de.iani.cubeConomy.CubeConomy;
import de.iani.cubeConomy.Messages;
import de.iani.cubeConomy.MoneyDatabaseException;
import de.iani.cubeConomy.MoneyException;
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
            sender.sendMessage(Component.text(commandString + getUsage()));
            return true;
        }
        double amount = args.getNext(0.0);
        amount = (Math.round(amount * 100)) / 100.0;
        if (!Double.isFinite(amount) || amount < 0.01) {
            sender.sendMessage(Messages.error("Amount must be positive"));
            return true;
        }

        CachedPlayer player = plugin.getPlayerUUIDCache().getPlayerFromNameOrUUID(name);

        if (player == null) {
            sender.sendMessage(Messages.error("Unknown player"));
            return true;
        }

        if (senderPlayer.getUniqueId().equals(player.getUUID())) {
            sender.sendMessage(Messages.error("Cannot send money to yourself"));
            return true;
        }

        String reason = args.getAll(null);

        try {
            plugin.transferMoney(sender, senderPlayer.getUniqueId(), player.getUUID(), amount, Cause.PAY_COMMAND, reason);
            plugin.getLogger().info(sender.getName() + " has sent " + plugin.formatMoney(amount) + " to " + player.getName() + (reason == null ? "" : " with reason \"" + reason + "\""));

            Component reasonMessage = Messages.reason(reason, NamedTextColor.DARK_GREEN);
            sender.sendMessage(Messages.prefixed(Component.text("You have sent ", NamedTextColor.DARK_GREEN)
                    .append(Component.text(plugin.formatMoney(amount), NamedTextColor.WHITE))
                    .append(Component.text(" to ", NamedTextColor.DARK_GREEN))
                    .append(Component.text(player.getName(), NamedTextColor.WHITE))
                    .append(reasonMessage)
                    .append(Component.text(".", NamedTextColor.DARK_GREEN))));
            plugin.sendMessageTo(senderPlayer, player.getUUID(), Messages.prefixed(Component.text(sender.getName(), NamedTextColor.WHITE)
                    .append(Component.text(" has sent to you ", NamedTextColor.DARK_GREEN))
                    .append(Component.text(plugin.formatMoney(amount), NamedTextColor.WHITE))
                    .append(reasonMessage)
                    .append(Component.text(".", NamedTextColor.DARK_GREEN))));
        } catch (MoneyDatabaseException e) {
            sender.sendMessage(Messages.error("Database error: " + e.getMessage()));
        } catch (MoneyException e) {
            sender.sendMessage(Messages.error(e.getMessage()));
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
