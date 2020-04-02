package de.iani.cubeConomy.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class MoneyChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private UUID player;
    private double amount;
    private long timestamp;

    public MoneyChangeEvent(UUID player, double amount, long timestamp) {
        this.player = player;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public UUID getPlayer() {
        return player;
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
