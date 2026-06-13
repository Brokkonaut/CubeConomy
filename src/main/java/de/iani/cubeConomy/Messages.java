package de.iani.cubeConomy;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class Messages {
    public static final Component PREFIX = Component.text("[", NamedTextColor.DARK_GREEN)
            .append(Component.text("Money", NamedTextColor.WHITE))
            .append(Component.text("] ", NamedTextColor.DARK_GREEN));

    private Messages() {
    }

    public static Component prefixed(Component message) {
        return PREFIX.append(message);
    }

    public static Component error(String message) {
        return prefixed(Component.text(message, NamedTextColor.RED));
    }

    public static Component reason(String reason, NamedTextColor color) {
        if (reason == null) {
            return Component.empty();
        }
        return Component.text(" for ", color)
                .append(Component.text(reason, NamedTextColor.WHITE));
    }
}
