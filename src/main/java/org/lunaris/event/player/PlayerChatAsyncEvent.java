package org.lunaris.event.player;

import org.lunaris.entity.Player;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * Created by RINES on 16.09.17.
 */
public class PlayerChatAsyncEvent extends Event implements Cancellable {

    private final Player player;
    private String message;
    private boolean cancelled;

    public PlayerChatAsyncEvent(Player player, String message) {
        this.player = player;
        this.message = message;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
