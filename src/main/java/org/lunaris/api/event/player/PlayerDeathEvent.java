package org.lunaris.api.event.player;

import org.lunaris.entity.LPlayer;
import org.lunaris.api.event.Cancellable;
import org.lunaris.api.event.Event;

/**
 * Created by RINES on 25.09.17.
 */
public class PlayerDeathEvent extends Event implements Cancellable {

    private final LPlayer player;
    private boolean cancelled;

    public PlayerDeathEvent(LPlayer player) {
        this.player = player;
    }

    public LPlayer getPlayer() {
        return player;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

}
