package org.lunaris.entity.misc;

import org.lunaris.api.util.math.Vector3d;
import org.lunaris.api.world.BlockFace;
import org.lunaris.api.world.Location;
import org.lunaris.world.LWorld;

/**
 * Created by RINES on 04.10.17.
 */
public interface Movable {

    float getX();

    float getY();

    float getZ();

    float getMotionX();

    float getMotionY();

    float getMotionZ();

    float getYaw();

    float getHeadYaw();

    float getPitch();

    Location getLocation(LWorld world);

    void setPosition(float x, float y, float z);

    void setRotation(float yaw, float headYaw, float pitch);

    void setMotion(float x, float y, float z);

    default void move(float dx, float dy, float dz) {
        setPosition(getX() + dx, getY() + dy, getZ() + dz);
    }

    default void rotate(float dyaw, float dHeadYaw, float dpitch) {
        setRotation(getYaw() + dyaw, getHeadYaw() + dHeadYaw, getPitch() + dpitch);
    }

    default void rotate(float dyaw, float dpitch) {
        rotate(dyaw, dyaw, dpitch);
    }

    default void changeMotion(float dMotionX, float dMotionY, float dMotionZ) {
        setMotion(getMotionX() + dMotionX, getMotionY() + dMotionY, getMotionZ() + dMotionZ);
    }

    default void changeMotion(Vector3d velocity) {
        changeMotion((float) velocity.getX(), (float) velocity.getY(), (float) velocity.getZ());
    }

    default void setRotation(float yaw, float pitch) {
        setRotation(yaw, yaw, pitch);
    }

    default void setPositionAndRotation(float x, float y, float z, float yaw, float headYaw, float pitch) {
        setPosition(x, y, z);
        setRotation(yaw, headYaw, pitch);
    }

    default void setPositionAndRotation(float x, float y, float z, float yaw, float pitch) {
        setPositionAndRotation(x, y, z, yaw, yaw, pitch);
    }

    default void setPosition(Vector3d vector) {
        setPosition((float) vector.getX(), (float) vector.getY(), (float) vector.getZ());
    }

    default void setPositionAndRotation(Location location) {
        setPositionAndRotation((float) location.getX(), (float) location.getY(), (float) location.getZ(),
                (float) location.getYaw(), (float) location.getHeadYaw(), (float) location.getPitch());
    }

    default void setVelocity(Vector3d vector) {
        setMotion((float) vector.getX(), (float) vector.getY(), (float) vector.getZ());
    }

    default void setMotion(Vector3d vector) {
        setVelocity(vector);
    }

    Vector3d getDirection();

    default Vector3d getHeadDirection() {
        double rY = Math.toRadians(getHeadYaw());
        double rP = Math.toRadians(getPitch());
        double sinY = Math.sin(rY);
        double cosY = Math.cos(rY);
        double sinP = Math.sin(rP);
        double cosP = Math.cos(rP);
        return new Vector3d(cosY * cosP, sinP, sinY * cosP);
    }

    default BlockFace getTargetBlockFace() {
        double rotation = getHeadYaw() % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if ((0 <= rotation && rotation < 45) || (315 <= rotation && rotation < 360)) {
            return BlockFace.SOUTH;
        } else if (45 <= rotation && rotation < 135) {
            return BlockFace.WEST;
        } else if (135 <= rotation && rotation < 225) {
            return BlockFace.NORTH;
        } else if (225 <= rotation && rotation < 315) {
            return BlockFace.EAST;
        } else {
            return null;
        }
    }

}
