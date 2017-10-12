package org.lunaris.world.tileentity;

import org.lunaris.api.tileentity.TileEntity;
import org.lunaris.api.world.Block;
import org.lunaris.api.world.Location;
import org.lunaris.api.world.World;

/**
 * Created by RINES on 12.10.17.
 */
public abstract class LTileEntity implements TileEntity {

    private final Location location;

    LTileEntity(Location location) {
        this.location = location;
    }

    @Override
    public Block getBlock() {
        return this.location.getWorld().getBlockAt(this.location);
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public World getWorld() {
        return this.location.getWorld();
    }

    public abstract void tick();

}
