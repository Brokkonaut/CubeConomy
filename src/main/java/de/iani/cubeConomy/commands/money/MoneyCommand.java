package de.iani.cubeConomy.commands.money;

import java.util.ArrayList;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.iani.cubeConomy.CubeConomy;
import de.iani.cubeConomy.Messages;
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
            sender.sendMessage(Messages.error("Unknown player"));
            return true;
        }

        try {
            double money = plugin.getMoney(player.getUUID());
            if (name == null) {
                sender.sendMessage(Messages.prefixed(Component.text("Balance: ", NamedTextColor.DARK_GREEN)
                        .append(Component.text(plugin.formatMoney(money), NamedTextColor.WHITE))));
            } else {
                sender.sendMessage(Messages.prefixed(Component.text(player.getName() + "'s Balance: ", NamedTextColor.DARK_GREEN)
                        .append(Component.text(plugin.formatMoney(money), NamedTextColor.WHITE))));
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
