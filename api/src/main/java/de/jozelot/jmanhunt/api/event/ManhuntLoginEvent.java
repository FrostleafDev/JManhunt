package de.jozelot.jmanhunt.api.event;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ManhuntLoginEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private DisallowReason reason;
    private Result result;
    private Component message;

    public ManhuntLoginEvent(@NotNull Player player, @NotNull DisallowReason reason, @NotNull Component message) {
        this.player = player;
        this.reason = reason;
        this.result = Result.DENIED;
        this.message = message;
    }

    public enum Result {
        ALLOWED,
        DENIED;
    }

    public DisallowReason getReason() { return reason; }

    public Result getResult() {
        return result;
    }

    public Player getPlayer() {
        return player;
    }

    public Component getMessage() {
        return message;
    }

    public void setReason(DisallowReason reason) {
        this.reason = reason;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public void setMessage(Component message) {
        this.message = message;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
