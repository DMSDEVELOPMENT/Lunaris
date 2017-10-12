package org.lunaris.api.event.player;

import org.lunaris.entity.LPlayer;
import org.lunaris.api.event.Cancellable;
import org.lunaris.api.event.Event;

/**
 * Created by RINES on 14.09.17.
 */
public class PlayerLoginEvent extends Event implements Cancellable {

    private final LPlayer player;
    private boolean cancelled;

    public PlayerLoginEvent(LPlayer player) {
        this.player = player;
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

}
