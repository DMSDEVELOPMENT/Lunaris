package org.lunaris.event.inventory;

import org.lunaris.entity.LPlayer;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;
import org.lunaris.inventory.Inventory;

/**
 * Created by RINES on 01.10.17.
 */
public class InventoryOpenEvent extends Event implements Cancellable {

    private final Inventory inventory;
    private final LPlayer player;
    private boolean cancelled;

    public InventoryOpenEvent(Inventory inventory, LPlayer player) {
        this.inventory = inventory;
        this.player = player;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public LPlayer getPlayer() {
        return this.player;
    }

}
