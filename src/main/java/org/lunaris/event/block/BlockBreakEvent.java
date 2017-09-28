package org.lunaris.event.block;

import org.lunaris.block.Block;
import org.lunaris.entity.Player;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * Created by RINES on 28.09.17.
 */
public class BlockBreakEvent extends Event implements Cancellable {

    private final Player player;
    private final Block block;
    private boolean cancelled;

    public BlockBreakEvent(Player player, Block block) {
        this.player = player;
        this.block = block;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Block getBlock() {
        return this.block;
    }

}
