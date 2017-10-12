package org.lunaris.api.world;

import org.lunaris.api.entity.Player;
import org.lunaris.api.util.math.Vector3d;

import java.util.Collection;

/**
 * Created by RINES on 12.10.17.
 */
public interface Chunk {

    /**
     * Get the block in this chunk at coords x, y, z.
     * @param x full block's x coordinate (not chunk-relative).
     * @param y full block's y coordinate (not chunk-relative).
     * @param z full block's z coordinate (not chunk-relative).
     * @return block at given position (air block whether it's empty) or null, in case of y less than 0 or y bigger than 255.
     */
    Block getBlock(int x, int y, int z);

    /**
     * Get the block in this chunk at given location.
     * @param position full block's position (not chunk-relative).
     * @return block at given position (air block whether it's empty) or null, in case of y less than 0 or y bigger than 255.
     */
    default Block getBlock(Vector3d position) {
        return getBlock(position.getBlockX(), position.getBlockY(), position.getBlockZ());
    }

    /**
     * Get this chunk's world.
     * @return this chunk's world.
     */
    World getWorld();

    /**
     * Get this chunk's x coordinate.
     * @return this chunk's x coordinate.
     */
    int getX();

    /**
     * Get this chunk's z coordinate.
     * @return this chunk's z coordinate.
     */
    int getZ();

    /**
     * Get players that are in range of view of this chunk.
     * @return players that are in range of view of this chunk.
     */
    Collection<? extends Player> getWatcherPlayers();

    /**
     * Check whether this chunk is loaded.
     * @return if this chunk is loaded.
     */
    boolean isLoaded();

    /**
     * Unload this chunk.
     */
    void unload();

    /**
     * Check whether this chunk is in range of view of given player.
     * @param player player to check for.
     * @return whether this chunk is in range of view of given player.
     */
    boolean isInRangeOfViewFor(Player player);

}
