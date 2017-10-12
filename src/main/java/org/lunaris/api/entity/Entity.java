package org.lunaris.api.entity;

import org.lunaris.api.world.Chunk;
import org.lunaris.api.world.Location;
import org.lunaris.api.world.World;

import java.util.Collection;

/**
 * Created by RINES on 12.10.17.
 */
public interface Entity {

    /**
     * Get this entity's id.
     * @return this entity's id.
     */
    long getEntityID();

    /**
     * Get this entity's creation time.
     * @return this entity's creation time in unix-timestamp format.
     */
    long getCreationTime();

    /**
     * Get this entity's world.
     * @return this entity's world.
     */
    World getWorld();

    /**
     * Get this entity's type.
     * @return this entity's type.
     */
    EntityType getEntityType();

    /**
     * Check whether this entity is on ground.
     * @return if this entity is on ground.
     */
    boolean isOnGround();

    /**
     * Check whether this entity is inside of water/lava block.
     * @return if this entity is inside of water/lava block.
     */
    boolean isInsideOfWater();

    /**
     * Teleport this entity to given location.
     * @param location location to teleport to.
     */
    void teleport(Location location);

    /**
     * Get this entity's location.
     * @return this entity's location.
     */
    Location getLocation();

    /**
     * Get chunk this entity is currently in.
     * @return chunk this entity is currently in.
     */
    Chunk getChunk();

    /**
     * Get this entity's x coordinate.
     * @return this entity's x coordinate.
     */
    float getX();

    /**
     * Get this entity's y coordinate.
     * @return this entity's y coordinate.
     */
    float getY();

    /**
     * Get this entity's z coordinate.
     * @return this entity's z coordinate.
     */
    float getZ();

    /**
     * Get this entity's yaw.
     * @return this entity's yaw.
     */
    float getYaw();

    /**
     * Get this entity's head's yaw.
     * Usually it's the same as entity's yaw, but sometimes it can differ.
     * @return this entity's head's yaw.
     */
    float getHeadYaw();

    /**
     * Get this entity's pitch.
     * @return this entity's pitch.
     */
    float getPitch();

    /**
     * Set new position to this entity (actually, teleport entity to it without clearing it's yaw, head yaw and pitch).
     * @param x new x coordinate.
     * @param y new y coordinate.
     * @param z new z coordinate.
     */
    void setPosition(double x, double y, double z);

    /**
     * Set new rotation to this entity.
     * @param yaw new yaw.
     * @param headYaw new entity's head's yaw.
     * @param pitch new pitch.
     */
    void setRotation(double yaw, double headYaw, double pitch);

    /**
     * @see Entity#setRotation(double, double, double)
     * @param yaw new yaw & head's yaw.
     * @param pitch new pitch.
     */
    default void setRotation(double yaw, double pitch) {
        setRotation(yaw, yaw, pitch);
    }

    /**
     * Get this entity's display name.
     * It's a thing usually displayed above entity's head.
     * @return this entity's display name.
     */
    String getDisplayName();

    /**
     * Setup new entity's display name.
     * @see Entity#getDisplayName()
     * @param displayName new entity's display name.
     */
    void setDisplayName(String displayName);

    /**
     * Set this entity's display name visibility state.
     * @param visible if this entity's display name is visible at all. If set to false, second argument is meaningless.
     * @param always if this entity's display name is visible always. If set to false, display name will be visible only when
     *               looking directly and closely to the entity.
     */
    void setDisplayNameVisible(boolean visible, boolean always);

    /**
     * @see Entity#setDisplayNameVisible(boolean, boolean)
     * @return if this entity's display name is visible.
     */
    boolean isDisplayNameVisible();

    /**
     * @see Entity#setDisplayNameVisible(boolean, boolean)
     * @return if this entity's display name is visible and, moreover, is always visible.
     */
    boolean isDisplayNameAlwaysVisible();

    /**
     * Despawns this entity from the world.
     */
    void remove();

    /**
     * Get amount of ticks this entity will be on fire.
     * @return amount of ticks this entity will be on fire.
     */
    int getFireTicks();

    /**
     * Set entity to be on fire for given amount of ticks.
     * @param ticks amount of ticks on fire.
     */
    void setOnFire(int ticks);

    /**
     * Get height of this entity.
     * @return height of this entity.
     */
    float getHeight();

    /**
     * Get width of this entity.
     * @return width of this entity.
     */
    float getWidth();

    /**
     * Get range of this entity's tracking.
     * @return range of this entity's tracking.
     */
    int getTrackRange();

    /**
     * Get players, for whom this entity is in range of their view.
     * @return those players collection.
     */
    Collection<? extends Player> getWatchers();

}
