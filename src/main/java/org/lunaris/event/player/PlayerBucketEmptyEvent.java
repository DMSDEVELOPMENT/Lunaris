package org.lunaris.event.player;

import org.lunaris.block.LBlock;
import org.lunaris.entity.LPlayer;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * @author xtrafrancyz
 */
public class PlayerBucketEmptyEvent extends Event implements Cancellable {
    private LPlayer player;
    private LBlock target;
    private boolean cancelled = false;

    public PlayerBucketEmptyEvent(LPlayer player, LBlock target) {
        this.player = player;
        this.target = target;
    }

    public LPlayer getPlayer() {
        return this.player;
    }

    public LBlock getTargetBlock() {
        return this.target;
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
