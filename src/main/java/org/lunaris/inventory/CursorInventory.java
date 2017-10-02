package org.lunaris.inventory;

import org.lunaris.entity.Player;
import org.lunaris.inventory.transaction.InventorySection;

/**
 * Created by RINES on 01.10.17.
 */
public class CursorInventory extends Inventory {

    public CursorInventory(Player player) {
        super(InventoryType.CURSOR);
    }

    @Override
    int getReservedInventoryId() {
        return InventorySection.CURSOR.getId();
    }

}
