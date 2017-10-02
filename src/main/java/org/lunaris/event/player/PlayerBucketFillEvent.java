package org.lunaris.event.player;

import org.lunaris.block.Block;
import org.lunaris.entity.Player;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * @author xtrafrancyz
 */
public class PlayerBucketFillEvent extends Event implements Cancellable {
    private Player player;
    private Block target;
    private boolean cancelled = false;

    public PlayerBucketFillEvent(Player player, Block target) {
        this.player = player;
        this.target = target;
    }
    
    public Player getPlayer() {
        return this.player;
    }

    public Block getTargetBlock() {
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
