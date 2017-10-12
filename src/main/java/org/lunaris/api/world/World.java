package org.lunaris.api.world;

import org.lunaris.api.entity.Entity;
import org.lunaris.api.entity.Player;
import org.lunaris.api.util.math.Vector3d;
import org.lunaris.world.Dimension;

import java.util.Collection;

/**
 * Created by RINES on 12.10.17.
 */
public interface World {

    /**
     * Get block at given coordinates.
     * @param x block's x coordinate.
     * @param y block's y coordinate.
     * @param z block's z coordinate.
     * @return block at given coordinates (with air material whether it's empty) or null, if y is less than 0 or bigger than 255.
     */
    Block getBlockAt(int x, int y, int z);

    /**
     * @see World#getBlockAt(int, int, int)
     * @param position block's position.
     * @return block at given coordinates (with air material whether it's empty) or null, if y is less than 0 or bigger than 255.
     */
    Block getBlockAt(Vector3d position);

    /**
     * Get chunk at given chunk's coordinates.
     * @param chunkX chunk's x coordinate.
     * @param chunkZ chunk's z coordinate.
     * @return chunk object, whether it's loaded; null otherwise.
     */
    Chunk getChunkAt(int chunkX, int chunkZ);

    /**
     * Get chunk at given chunk's coordinates and preload it, whether it is still unloaded.
     * @param chunkX chunk's x coordinate.
     * @param chunkZ chunk's z coordinate.
     * @return chunk object, whether it was loaded or we succeeded loading it; null otherwise.
     */
    Chunk loadChunk(int chunkX, int chunkZ);

    /**
     * Unload chunk at given chunk's coordinates.
     * @param chunkX chunk's x coordinate.
     * @param chunkZ chunk's z coordinate.
     */
    void unloadChunk(int chunkX, int chunkZ);

    /**
     * Unload given chunk.
     * @param chunk the chunk itself.
     */
    void unloadChunk(Chunk chunk);

    /**
     * Get world's time.
     * @return world's time in ticks.
     */
    int getTime();

    /**
     * Get all world's entities.
     * @return all world's entities collection.
     */
    Collection<? extends Entity> getEntities();

    /**
     * Get all world's players.
     * @return all world's players collection.
     */
    Collection<? extends Player> getPlayers();

    /**
     * Get world's entity by it's id.
     * @param entityID id of the entity.
     * @return null if entity is not present in this world; entity instance otherwise.
     */
    Entity getEntityById(long entityID);

    /**
     * Get entities, close to given position.
     * @param position the position.
     * @param radius maximum radius in circle of which we are collecting entities.
     * @return those entities collection.
     */
    Collection<? extends Entity> getNearbyEntities(Vector3d position, double radius);

    /**
     * Get entities of given class, close to given position.
     * @param entityClass entities' class.
     * @param position the position.
     * @param radius maximum radius in circle of which we are collecting entities.
     * @param <T> entity type.
     * @return those entities collection.
     */
    <T extends Entity> Collection<T> getNearbyEntitiesByClass(Class<T> entityClass, Vector3d position, double radius);

    /**
     * Get players, that are in range of view of given position.
     * @param position the position itself.
     * @return those players collection.
     */
    Collection<? extends Player> getWatcherPlayers(Vector3d position);

    /**
     * Get this world's dimension (overworld / nether / the_end).
     * @return this world's dimension.
     */
    Dimension getDimension();

    /**
     * Get spawn location for this world.
     * @return spawn location for this world.
     */
    Location getSpawnLocation();

}
