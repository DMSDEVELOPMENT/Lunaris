package org.lunaris.entity.misc;

import org.lunaris.Lunaris;
import org.lunaris.util.math.Vector3d;
import org.lunaris.world.Location;
import org.lunaris.world.World;

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

    Location getLocation(World world);

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

    default Vector3d getDirection() {
        double rx = Math.toRadians(getYaw()), ry = Math.toRadians(getPitch());
        double xz = Math.cos(ry);
        return new Vector3d(-xz * Math.sin(rx), -Math.sin(ry), xz * Math.cos(rx));
    }

    default Vector3d getHeadDirection() {
        double rY = Math.toRadians(getHeadYaw());
        double rP = Math.toRadians(getPitch());
        double sinY = Math.sin(rY);
        double cosY = Math.cos(rY);
        double sinP = Math.sin(rP);
        double cosP = Math.cos(rP);
        return new Vector3d(cosY * cosP, sinP, sinY * cosP);
    }

}
