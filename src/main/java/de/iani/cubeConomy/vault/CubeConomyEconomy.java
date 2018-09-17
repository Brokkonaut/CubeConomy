package de.iani.cubeConomy.vault;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.OfflinePlayer;

import de.iani.cubeConomy.CubeConomyAPI;
import de.iani.cubeConomy.MoneyDatabaseException;
import de.iani.cubeConomy.MoneyException;

public class CubeConomyEconomy implements Economy {
    private CubeConomyAPI cubeConomy;

    public CubeConomyEconomy(CubeConomyAPI cubeConomy) {
        this.cubeConomy = cubeConomy;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "CubeConomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public String currencyNamePlural() {
        return cubeConomy.getCurrencyNamePlural();
    }

    @Override
    public String currencyNameSingular() {
        return cubeConomy.getCurrencyName();
    }

    @Override
    public String format(double amount) {
        return cubeConomy.formatMoney(amount);
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    private UUID getPlayerUUID(String playerName) {
        return cubeConomy.getPlayerUUID(playerName);
    }

    public boolean createPlayerAccount(UUID playerUUID) {
        if (playerUUID == null) {
            return false;
        }
        try {
            cubeConomy.createAccount(playerUUID);
            return true;
        } catch (MoneyDatabaseException e) {
            return false;
        }
    }

    public double getBalance(UUID playerUUID) {
        if (playerUUID == null) {
            return 0.0;
        }
        try {
            return cubeConomy.getMoney(playerUUID);
        } catch (MoneyDatabaseException e) {
            return 0.0;
        }
    }

    public EconomyResponse depositPlayer(UUID playerUUID, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot desposit negative funds");
        }
        if (playerUUID == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Invalid player");
        }
        try {
            double newAmount = cubeConomy.changeMoney(playerUUID, amount);
            return new EconomyResponse(amount, newAmount, ResponseType.SUCCESS, null);
        } catch (MoneyDatabaseException e) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
        }
    }

    public EconomyResponse withdrawPlayer(UUID playerUUID, double amount) {
        if (amount < 0) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");
        }
        if (playerUUID == null) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, "Invalid player");
        }
        try {
            double newMoney = cubeConomy.withdrawMoney(playerUUID, amount);
            return new EconomyResponse(amount, newMoney, ResponseType.SUCCESS, null);
        } catch (MoneyException e) {
            return new EconomyResponse(0, getBalance(playerUUID), ResponseType.FAILURE, e.getMessage());
        } catch (MoneyDatabaseException e) {
            return new EconomyResponse(0, 0, ResponseType.FAILURE, e.getMessage());
        }
    }

    @Override
    @Deprecated
    public boolean createPlayerAccount(String player) {
        return createPlayerAccount(getPlayerUUID(player));
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return createPlayerAccount(player.getUniqueId());
    }

    @Override
    @Deprecated
    public boolean createPlayerAccount(String player, String world) {
        return createPlayerAccount(player);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String world) {
        return createPlayerAccount(player);
    }

    @Override
    @Deprecated
    public boolean hasAccount(String player) {
        return createPlayerAccount(player);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return createPlayerAccount(player);
    }

    @Override
    @Deprecated
    public boolean hasAccount(String player, String world) {
        return hasAccount(player);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String world) {
        return hasAccount(player);
    }

    @Override
    @Deprecated
    public double getBalance(String player) {
        return getBalance(getPlayerUUID(player));
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return getBalance(player.getUniqueId());
    }

    @Override
    @Deprecated
    public double getBalance(String player, String world) {
        return getBalance(player);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    @Deprecated
    public boolean has(String player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    @Deprecated
    public boolean has(String player, String world, double amount) {
        return has(player, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String world, double amount) {
        return has(player, amount);
    }

    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String player, double amount) {
        return depositPlayer(getPlayerUUID(player), amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return depositPlayer(player.getUniqueId(), amount);
    }

    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String player, String world, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String world, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String player, double amount) {
        return withdrawPlayer(getPlayerUUID(player), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return withdrawPlayer(player.getUniqueId(), amount);
    }

    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String player, String world, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String world, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse bankBalance(String bank) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse bankDeposit(String bank, double amount) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse bankHas(String bank, double amount) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse bankWithdraw(String bank, double amount) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    @Deprecated
    public EconomyResponse createBank(String bank, String player) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse createBank(String bank, OfflinePlayer player) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse deleteBank(String bank) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    @Deprecated
    public EconomyResponse isBankMember(String bank, String player) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse isBankMember(String bank, OfflinePlayer player) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    @Deprecated
    public EconomyResponse isBankOwner(String bank, String player) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public EconomyResponse isBankOwner(String bank, OfflinePlayer player) {
        return new EconomyResponse(0.0, 0.0, ResponseType.NOT_IMPLEMENTED, "Banks are not supported");
    }

    @Override
    public List<String> getBanks() {
        return Collections.EMPTY_LIST;
    }
}
