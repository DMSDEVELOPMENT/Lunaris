package org.lunaris.event.player;

import org.lunaris.entity.Item;
import org.lunaris.entity.LPlayer;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * Created by RINES on 04.10.17.
 */
public class PlayerPickupItemEvent extends Event implements Cancellable {

    private final LPlayer player;
    private final Item item;
    private boolean cancelled;

    public PlayerPickupItemEvent(LPlayer player, Item item) {
        this.player = player;
        this.item = item;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public LPlayer getPlayer() {
        return this.player;
    }

    public Item getItem() {
        return this.item;
    }

}
