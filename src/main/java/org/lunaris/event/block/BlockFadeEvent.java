package org.lunaris.event.block;

import org.lunaris.block.Block;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

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
