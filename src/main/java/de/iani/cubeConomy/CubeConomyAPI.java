package de.iani.cubeConomy;

import java.util.UUID;

public interface CubeConomyAPI {
    /**
     * Format amount into a human readable string.
     * 
     * @param amount
     *            the amount
     * @return the formated amount
     */
    public String formatMoney(double amount);

    /**
     * Returns the name of the currency in singular form.
     * 
     * @return the name of the currency (singular)
     */
    public String getCurrencyName();

    /**
     * Returns the name of the currency in plural form.
     * 
     * @return the name of the currency (plural)
     */
    public String getCurrencyNamePlural();

    /**
     * Returns the cached UUID of a player or null if no UUID could be found.
     * 
     * @param playerName
     *            the name of the player
     * @return the UUID of the player or null
     */
    public UUID getPlayerUUID(String playerName);

    /**
     * Creates an account for the given player
     * 
     * @param player
     *            the UUID of the player, may not be null
     * @throws MoneyDatabaseException
     *             if the database access fails
     */
    public void createAccount(UUID player) throws MoneyDatabaseException;

    /**
     * Gets the money amount for the given player
     * 
     * @param player
     *            the UUID of the player, may not be null
     * @return the money amount for the player
     * @throws MoneyDatabaseException
     *             if the database access fails
     * 
     */
    public double getMoney(UUID player) throws MoneyDatabaseException;

    /**
     * Sets the money amount for the given player
     * 
     * @param player
     *            the UUID of the player, may not be null
     * @param amount
     *            the new money amount for the player
     * @throws MoneyDatabaseException
     *             if the database access fails
     * 
     */
    public void setMoney(UUID player, double amount) throws MoneyDatabaseException;

    /**
     * Changes the money amount for the given player
     * 
     * @param player
     *            the UUID of the player, may not be null
     * @param deltaAmount
     *            the amount to add to the players account
     * @return the new money amount for the player
     * @throws MoneyDatabaseException
     *             if the database access fails
     * 
     */
    public double changeMoney(UUID player, double deltaAmount) throws MoneyDatabaseException;

    /**
     * Withdraws money from the given player, if he has enough money
     * 
     * @param player
     *            the UUID of the player, may not be null
     * @param withdrawAmount
     *            the amount to withdraw from the players account
     * @return the new money amount for the player
     * @throws MoneyException
     *             if the player has not enough money
     * @throws MoneyDatabaseException
     *             if the database access fails
     * 
     */
    public double withdrawMoney(UUID player, double withdrawAmount) throws MoneyDatabaseException, MoneyException;

    /**
     * Transfers money from one player to another one, if the first one has enough money
     * 
     * @param fromPlayer
     *            the UUID of the player to remove money from, may not be null
     * @param toPlayer
     *            the UUID of the player to add money to, may not be null
     * @param amount
     *            the amount to move from the first players account to the second players account
     * @return the new money amount for the first player
     * @throws MoneyException
     *             if the first player has not enough money
     * @throws MoneyDatabaseException
     *             if the database access fails
     * 
     */
    public double transferMoney(UUID fromPlayer, UUID toPlayer, double amount) throws MoneyDatabaseException, MoneyException;
}
