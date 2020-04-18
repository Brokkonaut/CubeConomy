package de.iani.cubeConomy.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class UnknownTransactionEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private long timestamp;
    private UUID playerUUID;
    private double amount;

    public UnknownTransactionEvent(long timestamp, UUID playerUUID, double amount) {
        this.timestamp = timestamp;
        this.playerUUID = playerUUID;
        this.amount = amount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
