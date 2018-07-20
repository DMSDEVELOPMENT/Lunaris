package org.lunaris.api.world;

import org.lunaris.api.material.Material;
import org.lunaris.material.LBlockHandle;

/**
 * Created by RINES on 12.10.17.
 */
public interface Block {

    /**
     * Get this block's material.
     *
     * @return this block's material.
     */
    Material getType();

    /**
     * Get this block's material id.
     *
     * @return this block's material id.
     */
    int getTypeId();

    /**
     * Get this block's data.
     *
     * @return this block's data.
     */
    int getData();

    /**
     * Get this block's location.
     *
     * @return this block's location.
     */
    Location getLocation();

    /**
     * Get this block's world.
     *
     * @return this block's world.
     */
    World getWorld();

    /**
     * Get this block's x coordinate.
     *
     * @return this block's x coordinate.
     */
    int getX();

    /**
     * Get this block's y coordinate.
     *
     * @return this block's y coordinate.
     */
    int getY();

    /**
     * Get this block's z coordinate.
     *
     * @return this block's z coordinate.
     */
    int getZ();

    /**
     * Get chunk this block is in.
     *
     * @return chunk this block is in.
     */
    Chunk getChunk();

    /**
     * Get block next to the given side of this block.
     *
     * @param side the side we are looking to.
     * @return block next to the given side of this block.
     */
    Block getSide(BlockFace side);

    /**
     * Get this block's handle.
     *
     * @return this block's handle.
     */
    LBlockHandle getHandle();

    /**
     * Change this block's material to given one.
     * This block's data will be changed to 0.
     *
     * @param type new block's material.
     */
    void setType(Material type);

    /**
     * Change this block's material to another one by given id.
     * This block's data will be changed to 0.
     *
     * @param id new block's material's id.
     */
    void setTypeId(int id);

    /**
     * Change this block's data to given one.
     *
     * @param data new block's data
     */
    void setData(int data);

    /**
     * Change block's material and data to given ones.
     *
     * @param type new block's material.
     * @param data new block's data.
     */
    void setTypeAndData(Material type, int data);

    /**
     * Change block's material and data to given ones.
     *
     * @param id   new block's material's id.
     * @param data new block's data.
     */
    void setTypeIdAndData(int id, int data);

}
