package org.lunaris.inventory;

import org.lunaris.api.inventory.InventoryType;
import org.lunaris.world.tileentity.ChestTileEntity;

/**
 * Created by RINES on 12.10.17.
 */
public class ChestInventory extends LContainerInventory {

    public ChestInventory(ChestTileEntity holder) {
        super(holder, InventoryType.CHEST);
    }

}
