package org.lunaris.api.event.inventory;

import org.lunaris.api.entity.Player;
import org.lunaris.api.event.Event;
import org.lunaris.inventory.LInventory;

/**
 * Created by RINES on 01.10.17.
 */
public class InventoryCloseEvent extends Event {

    private final LInventory inventory;
    private final Player player;

    public InventoryCloseEvent(LInventory inventory, Player player) {
        this.inventory = inventory;
        this.player = player;
    }

    public LInventory getInventory() {
        return this.inventory;
    }

    public Player getPlayer() {
        return this.player;
    }

}
