package org.lunaris.api.event.player;

import org.lunaris.api.event.Cancellable;
import org.lunaris.api.event.Event;
import org.lunaris.entity.LPlayer;

/**
 * Created by RINES on 13.09.17.
 */
public class PlayerPreLoginEvent extends Event implements Cancellable {

    private final LPlayer player;
    private boolean cancelled;

    public PlayerPreLoginEvent(LPlayer player) {
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
        return player;
    }

}
