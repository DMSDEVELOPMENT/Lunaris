package org.lunaris.event.player;

import org.lunaris.entity.Player;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * Created by RINES on 25.09.17.
 */
public class PlayerDeathEvent extends Event implements Cancellable {

    private final Player player;
    private boolean cancelled;

    public PlayerDeathEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
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
