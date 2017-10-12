package org.lunaris.event.inventory;

import org.lunaris.entity.LPlayer;
import org.lunaris.event.Event;
import org.lunaris.inventory.Inventory;

/**
 * Created by RINES on 01.10.17.
 */
public class InventoryCloseEvent extends Event {

    private final Inventory inventory;
    private final LPlayer player;

    public InventoryCloseEvent(Inventory inventory, LPlayer player) {
        this.inventory = inventory;
        this.player = player;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public LPlayer getPlayer() {
        return this.player;
    }

}
