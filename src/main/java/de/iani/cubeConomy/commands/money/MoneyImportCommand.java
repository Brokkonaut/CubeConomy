package de.iani.cubeConomy.commands.money;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.iani.cubeConomy.CubeConomy;
import de.iani.cubeConomy.Permissions;
import de.iani.cubeConomy.commands.ArgsParser;
import de.iani.cubeConomy.commands.SubCommand;

public class MoneyImportCommand extends SubCommand {
    private CubeConomy plugin;

    public MoneyImportCommand(CubeConomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getRequiredPermission() {
        return Permissions.CUBECONOMY_IMPORT;
    }

    @Override
    public String getUsage() {
        return "<table>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) {
        String name = args.getNext(null);

        if (name == null) {
            sender.sendMessage(commandString + getUsage());
            return true;
        }

        try {
            plugin.getPluginDatabase().importIConomy(plugin.getPlayerUUIDCache(), new File(plugin.getDataFolder(), "importfails.txt"), sender, name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public ArrayList<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        return new ArrayList<>();
    }

}
