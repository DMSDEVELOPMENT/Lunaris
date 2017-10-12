package org.lunaris.world.tileentity;

import org.lunaris.api.entity.Player;
import org.lunaris.api.tileentity.ContainerTileEntity;
import org.lunaris.api.world.Location;
import org.lunaris.inventory.LContainerInventory;

/**
 * Created by RINES on 12.10.17.
 */
public abstract class LContainerTileEntity extends LTileEntity implements ContainerTileEntity {

    private LContainerInventory inventory;

    LContainerTileEntity(Location location) {
        super(location);
        this.inventory = generateInventory();
    }

    public LContainerInventory getInventory() {
        return this.inventory;
    }

    public abstract void onInventoryOpened(Player player);

    public abstract void onInventoryClosed(Player player);

    abstract LContainerInventory generateInventory();

}
