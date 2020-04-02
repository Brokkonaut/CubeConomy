package de.iani.cubeConomy.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MoneyPayEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player from;
    private OfflinePlayer to;
    private double amount;
    private long timestamp;

    public MoneyPayEvent(Player from, OfflinePlayer to, double amount, long timestamp) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Player getFrom() {
        return from;
    }

    public OfflinePlayer getTo() {
        return to;
    }

    public double getAmount() {
        return amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
