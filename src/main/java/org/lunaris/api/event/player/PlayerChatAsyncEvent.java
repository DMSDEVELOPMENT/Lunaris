package org.lunaris.api.event.player;

import org.lunaris.api.event.Cancellable;
import org.lunaris.api.event.Event;
import org.lunaris.entity.LPlayer;

/**
 * Created by RINES on 16.09.17.
 */
public class PlayerChatAsyncEvent extends Event implements Cancellable {

    private final LPlayer player;
    private String message;
    private boolean cancelled;

    public PlayerChatAsyncEvent(LPlayer player, String message) {
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

    public LPlayer getPlayer() {
        return this.player;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
