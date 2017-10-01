package org.lunaris.inventory;

import org.lunaris.entity.Player;

/**
 * Created by RINES on 01.10.17.
 */
public class CursorInventory extends Inventory {

    public CursorInventory(Player player) {
        super(InventoryType.CURSOR);
    }

}
