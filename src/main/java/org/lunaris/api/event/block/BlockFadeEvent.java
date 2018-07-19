package org.lunaris.api.event.block;

import org.lunaris.api.event.Cancellable;
import org.lunaris.api.event.Event;
import org.lunaris.api.world.Block;

/**
 * @author xtrafrancyz
 */
public class BlockFadeEvent extends Event implements Cancellable {

    private final Block block;
    private boolean cancelled;

    public BlockFadeEvent(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return this.block;
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
