package de.iani.cubeConomy.data;

import org.bukkit.configuration.file.FileConfiguration;

import de.iani.cubeConomy.CubeConomy;

public class CubeConomyConfig {
    private CubeConomy plugin;
    private SQLConfig sqlConfig;
    private String currencyName;
    private String currencyNamePlural;
    private double initialMoney;
    private boolean bungeeBroadcast;

    public CubeConomyConfig(CubeConomy plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        reloadConfig();
    }

    private void reloadConfig() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        sqlConfig = new SQLConfig(config.getConfigurationSection("database"));
        currencyName = config.getString("name");
        currencyNamePlural = config.getString("nameplural");
        initialMoney = config.getDouble("initialMoney");
        if (Double.isNaN(initialMoney) || Double.isInfinite(initialMoney)) {
            initialMoney = 0;
        }
        bungeeBroadcast = config.getBoolean("bungeeBroadcast");
    }

    public double getDefaultMoney() {
        return initialMoney;
    }

    public SQLConfig getSQLConfig() {
        return sqlConfig;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public String getCurrencyNamePlural() {
        return currencyNamePlural;
    }

    public boolean bungeeBroadcast() {
        return bungeeBroadcast;
    }
}
