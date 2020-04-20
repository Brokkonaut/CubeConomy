package de.iani.cubeConomy.events;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

public class MoneyChangedEvent extends MoneyEvent {
    private static final HandlerList handlers = new HandlerList();

    public MoneyChangedEvent(CommandSender actor, double amount, UUID target, Cause cause) {
        super(actor, amount, target, cause);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public MoneyChangedEvent call() {
        Bukkit.getPluginManager().callEvent(this);
        return this;
    }
}
