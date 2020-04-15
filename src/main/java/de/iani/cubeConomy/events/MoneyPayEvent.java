package de.iani.cubeConomy.events;

import de.iani.playerUUIDCache.CachedPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MoneyPayEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player from;
    private CachedPlayer to;
    private double amount;
    private long timestamp;

    public MoneyPayEvent(Player from, CachedPlayer to, double amount, long timestamp) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Player getFrom() {
        return from;
    }

    public CachedPlayer getTo() {
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
