package org.lunaris.event.inventory;

import org.lunaris.entity.Player;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;
import org.lunaris.inventory.Inventory;

/**
 * Created by RINES on 01.10.17.
 */
public class InventoryOpenEvent extends Event implements Cancellable {

    private final Inventory inventory;
    private final Player player;
    private boolean cancelled;

    public InventoryOpenEvent(Inventory inventory, Player player) {
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

    public Player getPlayer() {
        return this.player;
    }

}
