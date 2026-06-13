package de.iani.cubeConomy.commands.money;

import de.iani.cubeConomy.CubeConomy;
import de.iani.cubeConomy.Messages;
import de.iani.cubeConomy.commands.ArgsParser;
import de.iani.cubeConomy.commands.SubCommand;
import de.iani.playerUUIDCache.CachedPlayer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

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
            sender.sendMessage(Messages.error("Database error"));
            return true;
        }
        sender.sendMessage(Component.text("-----[ ", NamedTextColor.DARK_GREEN)
                .append(Component.text("Wealthiest Accounts ", NamedTextColor.WHITE))
                .append(Component.text("]-----", NamedTextColor.DARK_GREEN)));
        int nr = 1;
        for (Entry<UUID, Double> e : accounts.entrySet()) {
            CachedPlayer p = plugin.getPlayerUUIDCache().getPlayer(e.getKey(), true);
            sender.sendMessage(Component.text((nr++) + ". ", NamedTextColor.DARK_GRAY)
                    .append(Component.text(String.valueOf(p != null ? p.getName() : e.getKey()), NamedTextColor.DARK_GREEN))
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(plugin.formatMoney(e.getValue()), NamedTextColor.WHITE)));
        }

        return true;
    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        return new ArrayList<>();
    }

}
