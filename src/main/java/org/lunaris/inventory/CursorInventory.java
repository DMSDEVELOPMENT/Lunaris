package org.lunaris.inventory;

import org.lunaris.api.inventory.InventoryType;
import org.lunaris.entity.LPlayer;
import org.lunaris.inventory.transaction.InventorySection;

/**
 * Created by RINES on 01.10.17.
 */
public class CursorInventory extends LInventory {

    public CursorInventory(LPlayer player) {
        super(InventoryType.CURSOR);
    }

    @Override
    int getReservedInventoryId() {
        return InventorySection.CURSOR.getId();
    }

}
