package org.lunaris.world.tileentity;

import org.lunaris.api.entity.Player;
import org.lunaris.api.world.Location;
import org.lunaris.inventory.ChestInventory;
import org.lunaris.inventory.ContainerInventory;

/**
 * Created by RINES on 12.10.17.
 */
public class ChestTileEntity extends ContainerTileEntity {

    public ChestTileEntity(Location location) {
        super(location);
    }

    @Override
    public void onInventoryOpened(Player player) {

    }

    @Override
    public void onInventoryClosed(Player player) {

    }

    @Override
    public ChestInventory getInventory() {
        return (ChestInventory) super.getInventory();
    }

    @Override
    ContainerInventory generateInventory() {
        return new ChestInventory(this);
    }

    @Override
    public void tick() {

    }

}
