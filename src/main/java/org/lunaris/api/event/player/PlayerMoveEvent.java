package org.lunaris.api.event.player;

import org.lunaris.entity.LPlayer;
import org.lunaris.api.event.Cancellable;
import org.lunaris.api.event.Event;

/**
 * Created by RINES on 15.09.17.
 */
public class PlayerMoveEvent extends Event implements Cancellable {

    private final LPlayer player;
    private final double x, y, z;
    private final double yaw, pitch;
    private boolean cancelled;

    public PlayerMoveEvent(LPlayer player, double x, double y, double z, double yaw, double pitch) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public LPlayer getPlayer() {
        return player;
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

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

}
