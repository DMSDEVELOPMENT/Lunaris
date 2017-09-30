package org.lunaris.event.entity;

import org.lunaris.entity.Entity;
import org.lunaris.event.Cancellable;
import org.lunaris.event.Event;

/**
 * Для игроков смотри PlayerMoveEvent.
 * Created by RINES on 30.09.17.
 */
public class EntityMoveEvent extends Event implements Cancellable {

    private final Entity entity;
    private final double x, y, z;
    private final double yaw, pitch;
    private boolean cancelled;

    public EntityMoveEvent(Entity entity, double x, double y, double z, double yaw, double pitch) {
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    public Entity getEntity() {
        return entity;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getYaw() {
        return yaw;
    }

    public double getPitch() {
        return pitch;
    }

}
