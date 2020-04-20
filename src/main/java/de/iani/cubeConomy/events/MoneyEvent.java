package de.iani.cubeConomy.events;

import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

public abstract class MoneyEvent extends Event {
    private final CommandSender actor;
    private final double amount;
    private final UUID target;
    private final Cause cause;

    public MoneyEvent(CommandSender actor, double amount, UUID target, Cause cause) {
        this.actor = actor;
        this.amount = amount;
        this.target = target;
        this.cause = cause;
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
}
