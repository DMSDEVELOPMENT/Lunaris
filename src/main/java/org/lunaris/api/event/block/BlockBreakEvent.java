package org.lunaris.api.event.block;

import org.lunaris.api.entity.Player;
import org.lunaris.api.world.Block;
import org.lunaris.api.event.Cancellable;
import org.lunaris.api.event.Event;

/**
 * Called in a moment when player breaks block (right before breaking).
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
