package org.lunaris.event.block;

import org.lunaris.block.Block;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * @author xtrafrancyz
 */
public class BlockFromToEvent extends Event implements Cancellable {
    private Block from;
    private Block to;
    private boolean cancelled;

    public BlockFromToEvent(Block from, Block to) {
        this.from = from;
        this.to = to;
    }

    public Block getFrom() {
        return this.from;
    }

    public Block getTo() {
        return this.to;
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
