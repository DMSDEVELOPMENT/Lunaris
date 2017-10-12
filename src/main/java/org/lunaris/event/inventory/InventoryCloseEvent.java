package org.lunaris.event.inventory;

import org.lunaris.api.entity.Player;
import org.lunaris.event.Event;
import org.lunaris.inventory.Inventory;

/**
 * Created by RINES on 01.10.17.
 */
public class InventoryCloseEvent extends Event {

    private final Inventory inventory;
    private final Player player;

    public InventoryCloseEvent(Inventory inventory, Player player) {
        this.inventory = inventory;
        this.player = player;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public Player getPlayer() {
        return this.player;
    }

}
