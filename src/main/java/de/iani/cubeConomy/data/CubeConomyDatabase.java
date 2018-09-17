package de.iani.cubeConomy.data;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.command.CommandSender;

import de.iani.cubeConomy.util.sql.MySQLConnection;
import de.iani.cubeConomy.util.sql.SQLConnection;
import de.iani.cubeConomy.util.sql.SQLRunnable;
import de.iani.playerUUIDCache.CachedPlayer;
import de.iani.playerUUIDCache.PlayerUUIDCache;

public class CubeConomyDatabase {
    private SQLConnection connection;

    private String tablePrefix;

    private final String createAccount;
    private final String changeMoney;

    private final String setMoney;

    private final String getMoney;

    private final String listTop;

    public CubeConomyDatabase(SQLConfig config) throws SQLException {
        connection = new MySQLConnection(config.getHost(), config.getDatabase(), config.getUser(), config.getPassword());
        this.tablePrefix = config.getTablePrefix();

        createAccount = "INSERT IGNORE INTO " + tablePrefix + "_money (id, money) VALUES (?, ?)";
        changeMoney = "INSERT INTO " + tablePrefix + "_money (id, money) VALUES (?, ?) ON DUPLICATE KEY UPDATE money = money + ?";
        setMoney = "INSERT INTO " + tablePrefix + "_money (id, money) VALUES (?, ?) ON DUPLICATE KEY UPDATE money = ?";
        getMoney = "SELECT money FROM " + tablePrefix + "_money WHERE id = ?";
        listTop = "SELECT id, money FROM " + tablePrefix + "_money ORDER BY money DESC LIMIT ?, ?";

        this.connection.runCommands(new SQLRunnable<Void>() {
            @Override
            public Void execute(Connection connection, SQLConnection sqlConnection) throws SQLException {
                Statement smt = connection.createStatement();
                if (!sqlConnection.hasTable(tablePrefix + "_money")) {
                    smt.executeUpdate("CREATE TABLE `" + tablePrefix + "_money` ("//
                            + "`id` VARCHAR( 50 ) NOT NULL ,"//
                            + "`money` DOUBLE NOT NULL, "//
                            + "PRIMARY KEY ( `id` )) ENGINE = innodb");
                }
                smt.close();
                return null;
            }
        });
    }

    public void disconnect() {
        connection.disconnect();
    }

    public double getMoney(final UUID player, final double initialMoney) throws SQLException {
        return this.connection.runCommands(new SQLRunnable<Double>() {
            @Override
            public Double execute(Connection connection, SQLConnection sqlConnection) throws SQLException {
                PreparedStatement smt = sqlConnection.getOrCreateStatement(getMoney);
                smt.setString(1, player.toString());
                ResultSet results = smt.executeQuery();
                double value = initialMoney;
                if (results.next()) {
                    value = results.getDouble("money");
                }
                results.close();
                return value;
            }
        });
    }

    public void createAccount(final UUID player, final double initialMoney) throws SQLException {
        this.connection.runCommands(new SQLRunnable<Void>() {
            @Override
            public Void execute(Connection connection, SQLConnection sqlConnection) throws SQLException {
                PreparedStatement smt = sqlConnection.getOrCreateStatement(createAccount);
                smt.setString(1, player.toString());
                smt.setDouble(2, initialMoney);
                smt.executeUpdate();
                return null;
            }
        });
    }

    public void setMoney(final UUID player, final double money) throws SQLException {
        this.connection.runCommands(new SQLRunnable<Void>() {
            @Override
            public Void execute(Connection connection, SQLConnection sqlConnection) throws SQLException {
                PreparedStatement smt = sqlConnection.getOrCreateStatement(setMoney);
                smt.setString(1, player.toString());
                smt.setDouble(2, money);
                smt.setDouble(3, money);
                smt.executeUpdate();
                return null;
            }
        });
    }

    public double changeMoney(final UUID player, final double deltaMoney, final double initialMoney) throws SQLException {
        return this.connection.runCommands(new SQLRunnable<Double>() {
            @Override
            public Double execute(Connection connection, SQLConnection sqlConnection) throws SQLException {
                PreparedStatement smt = sqlConnection.getOrCreateStatement(changeMoney);
                smt.setString(1, player.toString());
                smt.setDouble(2, initialMoney + deltaMoney);
                smt.setDouble(3, deltaMoney);
                smt.executeUpdate();

                smt = sqlConnection.getOrCreateStatement(getMoney);
                smt.setString(1, player.toString());
                ResultSet results = smt.executeQuery();
                double value = initialMoney;
                if (results.next()) {
                    value = results.getDouble("money");
                }
                results.close();
                return value;
            }
        });
    }

    public class MoneyAndSuccess {
        private final double newAmount;
        private final boolean success;

        public MoneyAndSuccess(double newAmount, boolean success) {
            this.newAmount = newAmount;
            this.success = success;
        }

        public double getNewAmount() {
            return newAmount;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public MoneyAndSuccess withdrawMoneyIfHas(final UUID player, final double deltaMoney, final double initialMoney) throws SQLException {
        return this.connection.runCommands(new SQLRunnable<MoneyAndSuccess>() {
            @Override
            public MoneyAndSuccess execute(Connection connection, SQLConnection sqlConnection) throws SQLException {
                PreparedStatement smt = sqlConnection.getOrCreateStatement(getMoney);
                smt.setString(1, player.toString());
                ResultSet results = smt.executeQuery();
                double value = initialMoney;
                if (results.next()) {
                    value = results.getDouble("money");
                }
                results.close();

                if (value < deltaMoney) {
                    return new MoneyAndSuccess(value, false);
                }

                smt = sqlConnection.getOrCreateStatement(changeMoney);
                smt.setString(1, player.toString());
                smt.setDouble(2, initialMoney - deltaMoney);
                smt.setDouble(3, -deltaMoney);
                smt.executeUpdate();

                return new MoneyAndSuccess(value - deltaMoney, true);
            }
        });
    }

    public MoneyAndSuccess sendMoneyIfHas(final UUID fromPlayer, final UUID toPlayer, final double deltaMoney, final double initialMoney) throws SQLException {
        return this.connection.runCommands(new SQLRunnable<MoneyAndSuccess>() {
            @Override
            public MoneyAndSuccess execute(Connection connection, SQLConnection sqlConnection) throws SQLException {
                PreparedStatement smt = sqlConnection.getOrCreateStatement(getMoney);
                smt.setString(1, fromPlayer.toString());
                ResultSet results = smt.executeQuery();
                double value = initialMoney;
                if (results.next()) {
                    value = results.getDouble("money");
                }
                results.close();

                if (value < deltaMoney) {
                    return new MoneyAndSuccess(value, false);
                }

                smt = sqlConnection.getOrCreateStatement(changeMoney);
                smt.setString(1, fromPlayer.toString());
                smt.setDouble(2, initialMoney - deltaMoney);
                smt.setDouble(3, -deltaMoney);
                smt.executeUpdate();

                smt = sqlConnection.getOrCreateStatement(changeMoney);
                smt.setString(1, toPlayer.toString());
                smt.setDouble(2, initialMoney + deltaMoney);
                smt.setDouble(3, deltaMoney);
                smt.executeUpdate();

                return new MoneyAndSuccess(value - deltaMoney, true);
            }
        });
    }

    public LinkedHashMap<UUID, Double> listTop(final int start, final int count) throws SQLException {
        return this.connection.runCommands(new SQLRunnable<LinkedHashMap<UUID, Double>>() {
            @Override
            public LinkedHashMap<UUID, Double> execute(Connection connection, SQLConnection sqlConnection) throws SQLException {
                LinkedHashMap<UUID, Double> rv = new LinkedHashMap<>();
                PreparedStatement smt = sqlConnection.getOrCreateStatement(listTop);
                smt.setInt(1, start);
                smt.setInt(2, count);
                ResultSet results = smt.executeQuery();
                while (results.next()) {
                    String id = results.getString("id");
                    double amount = results.getDouble("money");
                    try {
                        UUID uuid = UUID.fromString(id);
                        rv.put(uuid, amount);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
                results.close();
                return rv;
            }
        });
    }

    public void importIConomy(final PlayerUUIDCache uuidcache, final File failedAccountsFile, final CommandSender sender, final String table) throws SQLException {
        this.connection.runCommands(new SQLRunnable<Void>() {
            @Override
            public Void execute(Connection connection, SQLConnection sqlConnection) throws SQLException {
                sender.sendMessage("Starting import");
                if (!sqlConnection.hasTable(table)) {
                    sender.sendMessage("Unknown table");
                    return null;
                }
                HashMap<String, Double> amounts = new HashMap<>();
                PreparedStatement smt = connection.prepareStatement("SELECT username, balance FROM " + table);
                ResultSet rs = smt.executeQuery();
                while (rs.next()) {
                    amounts.put(rs.getString("username").toLowerCase(), rs.getDouble("balance"));
                }
                rs.close();
                int s = amounts.size();
                sender.sendMessage("Importing " + s + " UserIDs");
                int count = 0;
                Collection<CachedPlayer> res = uuidcache.getPlayers(amounts.keySet(), true);
                sender.sendMessage("Found " + res.size() + " UserIDs");

                for (CachedPlayer p : res) {
                    Double amount = amounts.remove(p.getName().toLowerCase());
                    if (amount != null) {
                        smt = sqlConnection.getOrCreateStatement(setMoney);
                        smt.setString(1, p.getUUID().toString());
                        smt.setDouble(2, amount);
                        smt.setDouble(3, amount);
                        smt.executeUpdate();
                        count++;
                    }
                }
                sender.sendMessage("Failed accounts: " + amounts.size());
                try {
                    FileOutputStream fos = new FileOutputStream(failedAccountsFile);
                    PrintWriter w = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(fos), Charset.forName("utf-8")));
                    for (Entry<String, Double> e : amounts.entrySet()) {
                        sender.sendMessage(e.getKey() + ": " + e.getValue());
                        w.println(e.getKey() + ": " + e.getValue());
                    }
                    w.close();
                } catch (Exception e) {
                    sender.sendMessage(e.getMessage());
                }
                sender.sendMessage("Successfully imported " + count + "/" + s + " accounts");
                return null;
            }
        });
    }
}
