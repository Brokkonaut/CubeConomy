package de.iani.cubeConomy.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.util.StringUtil;

public class CommandRouter implements CommandExecutor, TabCompleter {
    private class CommandMap {
        private String name;

        private CommandMap parent;

        private HashMap<String, CommandMap> subCommands;

        private ArrayList<CommandMap> subcommandsOrdered;

        private SubCommand executor;

        public CommandMap(CommandMap parent, String name) {
            this.parent = parent;
            this.name = name;
        }
    }

    private CommandMap commands;

    public CommandRouter(PluginCommand command) {
        commands = new CommandMap(null, null);
        command.setExecutor(this);
        command.setTabCompleter(this);
    }

    public void addCommandMapping(SubCommand command, String... route) {
        CommandMap current = commands;
        for (int i = 0; i < route.length; i++) {
            if (current.subCommands == null) {
                current.subCommands = new HashMap<>();
                current.subcommandsOrdered = new ArrayList<>();
            }
            String routePart = route[i].toLowerCase();
            CommandMap part = current.subCommands.get(routePart);
            if (part == null) {
                part = new CommandMap(current, routePart);
                current.subCommands.put(routePart, part);
                current.subcommandsOrdered.add(part);
            }
            current = part;
        }
        if (current.executor != null) {
            throw new IllegalArgumentException("Path " + Arrays.toString(route) + " is already mapped!");
        }
        current.executor = command;
    }

    public void addAlias(String alias, String... route) {
        if (route.length == 0) {
            throw new IllegalArgumentException("Route may not be empty!");
        }
        alias = alias.toLowerCase().trim();
        CommandMap current = commands;
        for (int i = 0; i < route.length - 1; i++) {
            if (current.subCommands == null) {
                throw new IllegalArgumentException("Path " + Arrays.toString(route) + " is not mapped!");
            }
            String routePart = route[i].toLowerCase();
            CommandMap part = current.subCommands.get(routePart);
            if (part == null) {
                throw new IllegalArgumentException("Path " + Arrays.toString(route) + " is not mapped!");
            }
            current = part;
        }
        CommandMap createAliasFor = current.subCommands.get(route[route.length - 1]);
        if (createAliasFor == null) {
            throw new IllegalArgumentException("Path " + Arrays.toString(route) + " is not mapped!");
        }
        if (current.subCommands.get(alias) != null) {
            route = route.clone();
            route[route.length - 1] = alias;
            throw new IllegalArgumentException("Path " + Arrays.toString(route) + " is already mapped!");
        }

        current.subCommands.put(alias, createAliasFor);
        // dont add to current.subcommandsOrdered, because it should not be shown in the help message
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String partial = args.length > 0 ? args[args.length - 1] : "";
        CommandMap currentMap = commands;
        int nr = 0;
        while (currentMap != null) {
            String currentCmdPart = args.length - 1 > nr ? args[nr] : null;
            if (currentCmdPart != null) {
                currentCmdPart = currentCmdPart.toLowerCase();
            }
            // descend to subcommand?
            if (currentCmdPart != null && currentMap.subCommands != null) {
                CommandMap subMap = currentMap.subCommands.get(currentCmdPart);
                if (subMap != null) {
                    nr += 1;
                    currentMap = subMap;
                    continue;
                }
            }
            ArrayList<String> rv = null;
            // get tabcomplete options from command
            if (currentMap.executor != null) {
                rv = currentMap.executor.onTabComplete(sender, command, alias, new ArgsParser(args, nr));
            }
            // get tabcomplete options from subcommands
            if (currentMap.subCommands != null) {
                if (rv == null) {
                    rv = new ArrayList<>();
                }
                for (Entry<String, CommandMap> e : currentMap.subCommands.entrySet()) {
                    String key = e.getKey();
                    if (StringUtil.startsWithIgnoreCase(key, partial)) {
                        CommandMap subcmd = e.getValue();
                        if (subcmd.executor == null || subcmd.executor.getRequiredPermission() == null || sender.hasPermission(subcmd.executor.getRequiredPermission())) {
                            if (sender instanceof Player || subcmd.executor == null || !subcmd.executor.requiresPlayer()) {
                                rv.add(key);
                            }
                        }
                    }
                }
            }
            if (rv != null) {
                rv = StringUtil.copyPartialMatches(partial, rv, new ArrayList<String>());
                Collections.sort(rv);
            }
            return rv;
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        CommandMap currentMap = commands;
        int nr = 0;
        while (currentMap != null) {
            String currentCmdPart = args.length > nr ? args[nr] : null;
            if (currentCmdPart != null) {
                currentCmdPart = currentCmdPart.toLowerCase();
            }
            // descend to subcommand?
            if (currentCmdPart != null && currentMap.subCommands != null) {
                CommandMap subMap = currentMap.subCommands.get(currentCmdPart);
                if (subMap != null) {
                    nr += 1;
                    currentMap = subMap;
                    continue;
                }
            }
            // execute this?
            SubCommand toExecute = currentMap.executor;
            if (toExecute != null) {
                if (toExecute.allowsCommandBlock() || !(sender instanceof BlockCommandSender || sender instanceof CommandMinecart)) {
                    if (!toExecute.requiresPlayer() || sender instanceof Player) {
                        if (toExecute.getRequiredPermission() == null || sender.hasPermission(toExecute.getRequiredPermission())) {
                            return toExecute.onCommand(sender, command, alias, getCommandString(alias, currentMap), new ArgsParser(args, nr));
                        } else {
                            sender.sendMessage(ChatColor.RED + "No permission!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "This command can only be executed by players!");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "This command is not allowed for CommandBlocks!");
                }
            }
            // show valid cmds
            showHelp(sender, alias, currentMap);
            return true;
        }
        return false;
    }

    private String getCommandString(String alias, CommandMap currentMap) {
        StringBuilder prefixBuilder = new StringBuilder();
        prefixBuilder.append('/').append(alias).append(' ');
        ArrayList<CommandMap> hierarchy = new ArrayList<>();
        CommandMap map = currentMap;
        while (map != null) {
            hierarchy.add(map);
            map = map.parent;
        }
        for (int i = hierarchy.size() - 2; i >= 0; i--) {
            prefixBuilder.append(hierarchy.get(i).name).append(' ');
        }
        return prefixBuilder.toString();
    }

    private void showHelp(CommandSender sender, String alias, CommandMap currentMap) {
        if (currentMap.subCommands != null) {
            String prefix = getCommandString(alias, currentMap);
            for (CommandMap subcmd : currentMap.subcommandsOrdered) {
                String key = subcmd.name;
                if (subcmd.executor == null) {
                    // hat weitere subcommands
                    sender.sendMessage(prefix + key + " ...");
                } else {
                    if (subcmd.executor.getRequiredPermission() == null || sender.hasPermission(subcmd.executor.getRequiredPermission())) {
                        if (sender instanceof Player || !subcmd.executor.requiresPlayer()) {

                            sender.sendMessage(prefix + key + " " + subcmd.executor.getUsage());
                        }
                    }
                }
            }
        } else {

        }
    }
}
