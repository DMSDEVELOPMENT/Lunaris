package org.lunaris.api.event.inventory;

import org.lunaris.api.entity.Player;
import org.lunaris.api.event.Cancellable;
import org.lunaris.api.event.Event;
import org.lunaris.inventory.LInventory;

/**
 * Created by RINES on 01.10.17.
 */
public class InventoryOpenEvent extends Event implements Cancellable {

    private final LInventory inventory;
    private final Player player;
    private boolean cancelled;

    public InventoryOpenEvent(LInventory inventory, Player player) {
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

    public LInventory getInventory() {
        return this.inventory;
    }

    public Player getPlayer() {
        return this.player;
    }

}
