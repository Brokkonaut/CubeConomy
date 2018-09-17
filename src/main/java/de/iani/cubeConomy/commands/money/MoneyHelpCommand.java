package de.iani.cubeConomy.commands.money;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.iani.cubeConomy.CubeConomy;
import de.iani.cubeConomy.Permissions;
import de.iani.cubeConomy.commands.ArgsParser;
import de.iani.cubeConomy.commands.SubCommand;

public class MoneyHelpCommand extends SubCommand {
    private CubeConomy plugin;

    public MoneyHelpCommand(CubeConomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {
        sender.sendMessage("");
        sender.sendMessage("CubeConomy " + plugin.getDescription().getVersion());
        sender.sendMessage("");
        sender.sendMessage("  /money" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Check your balance.");
        sender.sendMessage("  /money [name]" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Check others balance.");
        sender.sendMessage("  /money top" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "View top economical accounts.");
        sender.sendMessage("  /money pay [name] [amount]" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Send money to others.");
        if (sender.hasPermission(Permissions.CUBECONOMY_ADMIN)) {
            sender.sendMessage("  /money give [name] [amount]" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Give money.");
            sender.sendMessage("  /money take [name] [amount]" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Take money.");
            sender.sendMessage("  /money set [name] [amount]" + ChatColor.GOLD + " - " + ChatColor.YELLOW + "Set account balance.");
        }
        return true;
    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        return new ArrayList<>();
    }

}
