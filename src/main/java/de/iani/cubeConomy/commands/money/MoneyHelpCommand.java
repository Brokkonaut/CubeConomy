package de.iani.cubeConomy.commands.money;

import de.iani.cubeConomy.CubeConomy;
import de.iani.cubeConomy.Permissions;
import de.iani.cubeConomy.commands.ArgsParser;
import de.iani.cubeConomy.commands.SubCommand;
import java.util.ArrayList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class MoneyHelpCommand extends SubCommand {
    private CubeConomy plugin;

    public MoneyHelpCommand(CubeConomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text("CubeConomy " + plugin.getPluginMeta().getVersion()));
        sender.sendMessage(Component.empty());
        sender.sendMessage(helpLine("  /money", "Check your balance."));
        sender.sendMessage(helpLine("  /money [name]", "Check others balance."));
        sender.sendMessage(helpLine("  /money top", "View top economical accounts."));
        sender.sendMessage(helpLine("  /money pay [name] [amount]", "Send money to others."));
        if (sender.hasPermission(Permissions.CUBECONOMY_ADMIN)) {
            sender.sendMessage(helpLine("  /money give [name] [amount]", "Give money."));
            sender.sendMessage(helpLine("  /money take [name] [amount]", "Take money."));
            sender.sendMessage(helpLine("  /money set [name] [amount]", "Set account balance."));
        }
        return true;
    }

    private Component helpLine(String syntax, String description) {
        return Component.text(syntax)
                .append(Component.text(" - ", NamedTextColor.GOLD))
                .append(Component.text(description, NamedTextColor.YELLOW));
    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        return new ArrayList<>();
    }

}
