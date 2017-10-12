package org.lunaris.world.tileentity;

import org.lunaris.api.entity.Entity;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.world.BlockFace;
import org.lunaris.api.world.Location;
import org.lunaris.api.world.World;

/**
 * Created by RINES on 12.10.17.
 */
public abstract class TileEntity {

    private final Location location;

    TileEntity(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    public World getWorld() {
        return this.location.getWorld();
    }

    public abstract void tick();

}
