package org.lunaris.api.inventory;

import org.lunaris.api.tileentity.ContainerTileEntity;

/**
 * Created by RINES on 12.10.17.
 */
public interface ContainerInventory extends Inventory {

    /**
     * Get tile entity, corresponding to this inventory.
     *
     * @return tile entity, corresponding to this inventory.
     */
    ContainerTileEntity getHolder();

}
