package de.iani.cubeConomy.events;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

public class MoneyTransferedEvent extends MoneyEvent {
    private final UUID source;

    private static final HandlerList handlers = new HandlerList();

    public MoneyTransferedEvent(CommandSender actor, double amount, UUID source, UUID target, Cause cause, String reason) {
        super(actor, amount, target, cause, reason);
        this.source = source;
    }

    public UUID getSource() {
        return source;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public MoneyTransferedEvent call() {
        Bukkit.getPluginManager().callEvent(this);
        return this;
    }
}
