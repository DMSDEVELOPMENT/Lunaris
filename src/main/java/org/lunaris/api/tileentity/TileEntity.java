package org.lunaris.api.tileentity;

import org.lunaris.api.world.Block;
import org.lunaris.api.world.Location;
import org.lunaris.api.world.World;

/**
 * Created by RINES on 12.10.17.
 */
public interface TileEntity {

    /**
     * Get this tile entity's block.
     *
     * @return this tile entity's block.
     */
    Block getBlock();

    /**
     * Get this tile entity's location.
     *
     * @return this tile entity's location.
     */
    Location getLocation();

    /**
     * Get this tile entity's world.
     *
     * @return this tile entity's world.
     */
    World getWorld();

}
