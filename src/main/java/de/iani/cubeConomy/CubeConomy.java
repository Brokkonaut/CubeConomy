package de.iani.cubeConomy;

import de.iani.cubeConomy.commands.CommandRouter;
import de.iani.cubeConomy.commands.money.MoneyCommand;
import de.iani.cubeConomy.commands.money.MoneyGiveCommand;
import de.iani.cubeConomy.commands.money.MoneyHelpCommand;
import de.iani.cubeConomy.commands.money.MoneyImportCommand;
import de.iani.cubeConomy.commands.money.MoneyPayCommand;
import de.iani.cubeConomy.commands.money.MoneySetCommand;
import de.iani.cubeConomy.commands.money.MoneyTakeCommand;
import de.iani.cubeConomy.commands.money.MoneyTopCommand;
import de.iani.cubeConomy.data.CubeConomyConfig;
import de.iani.cubeConomy.data.CubeConomyDatabase;
import de.iani.cubeConomy.data.CubeConomyDatabase.MoneyAndSuccess;
import de.iani.cubeConomy.events.Cause;
import de.iani.cubeConomy.events.MoneyChangedEvent;
import de.iani.cubeConomy.events.MoneyTransferedEvent;
import de.iani.cubeConomy.vault.CubeConomyEconomy;
import de.iani.playerUUIDCache.CachedPlayer;
import de.iani.playerUUIDCache.PlayerUUIDCache;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.logging.Level;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class CubeConomy extends JavaPlugin implements CubeConomyAPI {
    private PlayerUUIDCache playerUUIDCache;
    private CubeConomyConfig config;
    private CubeConomyDatabase database;
    private DecimalFormat moneyFormat;
    private BungeeBroadcast broadcaster;

    public static final String MESSAGE_PREFIX = ChatColor.DARK_GREEN + "[" + ChatColor.WHITE + "Money" + ChatColor.DARK_GREEN + "] ";

    @Override
    public void onEnable() {
        playerUUIDCache = (PlayerUUIDCache) getServer().getPluginManager().getPlugin("PlayerUUIDCache");
        config = new CubeConomyConfig(this);
        try {
            database = new CubeConomyDatabase(config.getSQLConfig());
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Could not connect to database", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.moneyFormat = new DecimalFormat("###,##0.00");

        CommandRouter moneyCommand = new CommandRouter(getCommand("money"));
        moneyCommand.addCommandMapping(new MoneyCommand(this));

        moneyCommand.addCommandMapping(new MoneyHelpCommand(this), "help");
        moneyCommand.addCommandMapping(new MoneyPayCommand(this), "pay");
        moneyCommand.addCommandMapping(new MoneyTopCommand(this), "top");

        moneyCommand.addCommandMapping(new MoneyGiveCommand(this), "give");
        moneyCommand.addCommandMapping(new MoneyTakeCommand(this), "take");
        moneyCommand.addCommandMapping(new MoneySetCommand(this), "set");

        moneyCommand.addCommandMapping(new MoneyImportCommand(this), "import");

        CommandRouter payCommand = new CommandRouter(getCommand("pay"));
        payCommand.addCommandMapping(new MoneyPayCommand(this));

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            getServer().getServicesManager().register(Economy.class, new CubeConomyEconomy(this), this, ServicePriority.Highest);
            getLogger().info("Registered into Vault!");
        }

        if (config.bungeeBroadcast()) {
            broadcaster = new BungeeBroadcast(this);
        }
    }

    @Override
    public void onDisable() {
        if (database != null) {
            database.disconnect();
            database = null;
        }
    }

    public PlayerUUIDCache getPlayerUUIDCache() {
        return playerUUIDCache;
    }

    public CubeConomyConfig getPluginConfig() {
        return config;
    }

    public CubeConomyDatabase getPluginDatabase() {
        return database;
    }

    @Override
    public UUID getPlayerUUID(String playerName) {
        CachedPlayer p = getPlayerUUIDCache().getPlayer(playerName);
        return p == null ? null : p.getUUID();
    }

    @Override
    public String getCurrencyName() {
        return config.getCurrencyName();
    }

    @Override
    public String getCurrencyNamePlural() {
        return config.getCurrencyNamePlural();
    }

    @Override
    public String formatMoney(double amount) {
        String formated = moneyFormat.format(amount);
        if (amount == 1) {
            return formated + " " + getCurrencyName();
        }
        return formated + " " + getCurrencyNamePlural();
    }

    @Override
    public void createAccount(UUID player) throws MoneyDatabaseException {
        if (player == null) {
            throw new NullPointerException("player is null");
        }
        try {
            database.createAccount(player, config.getDefaultMoney());
            return;
        } catch (SQLException e) {
            throw new MoneyDatabaseException("Could not query database", e);
        }
    }

    @Override
    public double getMoney(UUID player) throws MoneyDatabaseException {
        if (player == null) {
            throw new NullPointerException("player is null");
        }
        try {
            return database.getMoney(player, config.getDefaultMoney());
        } catch (SQLException e) {
            throw new MoneyDatabaseException("Could not query database", e);
        }
    }

    @Override
    public void setMoney(UUID player, double money) throws MoneyDatabaseException {
        setMoney(null, player, money, Cause.PLUGIN, "");
    }

    public void setMoney(CommandSender actor, UUID player, double money, Cause cause, String reason) throws MoneyDatabaseException {
        if (player == null) {
            throw new NullPointerException("player is null");
        }
        try {
            double delta = database.setMoney(player, money, config.getDefaultMoney());
            new MoneyChangedEvent(actor, delta, player, cause, reason).call();
            return;
        } catch (SQLException e) {
            throw new MoneyDatabaseException("Could not query database", e);
        }
    }

    @Override
    public double changeMoney(UUID player, double deltaMoney) throws MoneyDatabaseException {
        return changeMoney(null, player, deltaMoney, Cause.PLUGIN, "");
    }

    public double changeMoney(CommandSender actor, UUID player, double deltaMoney, Cause cause, String reason) throws MoneyDatabaseException {
        if (player == null) {
            throw new NullPointerException("player is null");
        }
        try {
            double result = database.changeMoney(player, deltaMoney, config.getDefaultMoney());
            new MoneyChangedEvent(actor, deltaMoney, player, cause, reason).call();
            return result;
        } catch (SQLException e) {
            throw new MoneyDatabaseException("Could not query database", e);
        }
    }

    @Override
    public double transferMoney(UUID fromPlayer, UUID toPlayer, double amount) throws MoneyException, MoneyDatabaseException {
        return transferMoney(null, fromPlayer, toPlayer, amount, Cause.PLUGIN, "");
    }

    public double transferMoney(CommandSender actor, UUID fromPlayer, UUID toPlayer, double amount, Cause cause, String reason) throws MoneyException, MoneyDatabaseException {
        if (fromPlayer == null) {
            throw new NullPointerException("fromPlayer is null");
        }
        if (toPlayer == null) {
            throw new NullPointerException("toPlayer is null");
        }
        try {
            MoneyAndSuccess result = database.sendMoneyIfHas(fromPlayer, toPlayer, amount, config.getDefaultMoney());
            if (!result.isSuccess()) {
                throw new MoneyException("Insufficient funds");
            }
            new MoneyTransferedEvent(actor, amount, fromPlayer, toPlayer, cause, reason).call();
            return result.getNewAmount();
        } catch (SQLException e) {
            throw new MoneyDatabaseException("Could not query database", e);
        }
    }

    @Override
    public double withdrawMoney(UUID player, double withdrawMoney) throws MoneyException, MoneyDatabaseException {
        return withdrawMoney(null, player, withdrawMoney, Cause.PLUGIN, "");
    }

    public double withdrawMoney(CommandSender actor, UUID player, double withdrawMoney, Cause cause, String reason) throws MoneyException, MoneyDatabaseException {
        if (player == null) {
            throw new NullPointerException("player is null");
        }
        try {
            MoneyAndSuccess result = database.withdrawMoneyIfHas(player, withdrawMoney, config.getDefaultMoney());
            if (!result.isSuccess()) {
                throw new MoneyException("Insufficient funds");
            }
            new MoneyChangedEvent(actor, -withdrawMoney, player, cause, reason).call();
            return result.getNewAmount();
        } catch (SQLException e) {
            throw new MoneyDatabaseException("Could not query database", e);
        }
    }

    public void sendMessageTo(Player senderPlayer, UUID targetUUID, String message) {
        Player target = getServer().getPlayer(targetUUID);
        if (target != null) {
            target.sendMessage(message);
        } else if (getPluginConfig().bungeeBroadcast() && broadcaster != null) {
            broadcaster.sendMessage(senderPlayer, targetUUID, message);
        }
    }
}
