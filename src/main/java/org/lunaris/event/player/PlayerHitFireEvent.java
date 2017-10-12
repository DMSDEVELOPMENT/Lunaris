package org.lunaris.event.player;

import org.lunaris.block.LBlock;
import org.lunaris.entity.LPlayer;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * Created by RINES on 24.09.17.
 */
public class PlayerHitFireEvent extends Event implements Cancellable {

    private final LPlayer player;
    private final LBlock fire;
    private final LBlock block;
    private boolean cancelled;

    public PlayerHitFireEvent(LPlayer player, LBlock fire, LBlock block) {
        this.player = player;
        this.fire = fire;
        this.block = block;
    }

    public LPlayer getPlayer() {
        return player;
    }

    public LBlock getFire() {
        return fire;
    }

    public LBlock getBlock() {
        return block;
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
