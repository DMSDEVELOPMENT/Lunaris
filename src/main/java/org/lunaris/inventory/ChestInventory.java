package org.lunaris.inventory;

import org.lunaris.world.tileentity.ChestTileEntity;

/**
 * Created by RINES on 12.10.17.
 */
public class ChestInventory extends ContainerInventory {

    public ChestInventory(ChestTileEntity holder) {
        super(holder, InventoryType.CHEST);
    }

}
