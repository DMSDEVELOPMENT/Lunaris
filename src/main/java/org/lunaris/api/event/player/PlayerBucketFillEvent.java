package org.lunaris.api.event.player;

import org.lunaris.api.entity.Player;
import org.lunaris.api.event.Cancellable;
import org.lunaris.api.event.Event;
import org.lunaris.api.world.Block;

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
