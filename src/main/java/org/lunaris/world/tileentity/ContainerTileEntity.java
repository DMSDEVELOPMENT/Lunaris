package org.lunaris.world.tileentity;

import org.lunaris.api.entity.Player;
import org.lunaris.api.world.Location;
import org.lunaris.inventory.ContainerInventory;

/**
 * Created by RINES on 12.10.17.
 */
public abstract class ContainerTileEntity extends TileEntity {

    private String containerName;
    private ContainerInventory inventory;

    ContainerTileEntity(Location location) {
        super(location);
        this.inventory = generateInventory();
    }

    public String getContainerName() {
        return this.containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public ContainerInventory getInventory() {
        return this.inventory;
    }

    public abstract void onInventoryOpened(Player player);

    public abstract void onInventoryClosed(Player player);

    abstract ContainerInventory generateInventory();

}
