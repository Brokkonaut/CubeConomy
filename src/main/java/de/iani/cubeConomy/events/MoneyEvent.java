package de.iani.cubeConomy.events;

import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

public abstract class MoneyEvent extends Event {
    private final CommandSender actor;
    private final double amount;
    private final UUID target;
    private final Cause cause;
    private final String reason;

    public MoneyEvent(CommandSender actor, double amount, UUID target, Cause cause, String reason) {
        this.actor = actor;
        this.amount = amount;
        this.target = target;
        this.cause = cause;
        this.reason = reason;
    }

    public CommandSender getActor() {
        return actor;
    }

    public double getAmount() {
        return amount;
    }

    public UUID getTarget() {
        return target;
    }

    public Cause getCause() {
        return cause;
    }

    public String getReason() {
        return reason;
    }
}
