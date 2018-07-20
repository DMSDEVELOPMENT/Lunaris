package org.lunaris.api.tileentity;

import org.lunaris.api.inventory.ContainerInventory;

/**
 * Created by RINES on 12.10.17.
 */
public interface ContainerTileEntity extends TileEntity {

    /**
     * Get inventory, corresponding to this tile entity.
     *
     * @return inventory, corresponding to this tile entity.
     */
    ContainerInventory getInventory();

}
