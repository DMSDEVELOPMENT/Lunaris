package org.lunaris.event.player;

import org.lunaris.block.Block;
import org.lunaris.entity.Player;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * Created by RINES on 24.09.17.
 */
public class PlayerHitFireEvent extends Event implements Cancellable {

    private final Player player;
    private final Block fire;
    private final Block block;
    private boolean cancelled;

    public PlayerHitFireEvent(Player player, Block fire, Block block) {
        this.player = player;
        this.fire = fire;
        this.block = block;
    }

    public Player getPlayer() {
        return player;
    }

    public Block getFire() {
        return fire;
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
