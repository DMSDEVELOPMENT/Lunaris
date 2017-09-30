package org.lunaris.entity.data;

import org.lunaris.Lunaris;
import org.lunaris.entity.Entity;
import org.lunaris.event.entity.EntityMoveEvent;
import org.lunaris.network.protocol.packet.Packet12MoveEntity;
import org.lunaris.network.protocol.packet.Packet28SetEntityMotion;
import org.lunaris.util.math.Vector3d;
import org.lunaris.util.math.Vector3f;
import org.lunaris.world.Location;

/**
 * Created by RINES on 30.09.17.
 */
public class EntityMovement {

    private final Entity entity;
    protected float x, y, z, lastX, lastY, lastZ, yaw, pitch, lastYaw, lastPitch, motionX, motionY, motionZ,
        lastMotionX, lastMotionY, lastMotionZ;

    public EntityMovement(Entity entity) {
        this.entity = entity;
    }

    public void setPositionAndRotation(Location location) {
        setPosition(location);
        setRotation(location.getYaw(), location.getPitch());
    }

    public void setPositionAndRotation(double x, double y, double z, double yaw, double pitch) {
        setPosition(x, y, z);
        setRotation(yaw, pitch);
    }

    public void teleport(Location location) {
        setPositionAndRotation(location);
        setMotion(new Vector3d(0D, 0D, 0D));
    }

    public void setPosition(Vector3d position) {
        setPosition(position.getX(), position.getY(), position.getZ());
    }

    public void setPosition(double x, double y, double z) {
        this.x = (float) x;
        this.y = (float) y;
        this.z = (float) z;
    }

    public void setRotation(double yaw, double pitch) {
        this.yaw = (float) yaw;
        this.pitch = (float) pitch;
    }

    public void setMotion(Vector3d motion) {
        this.motionX = (float) motion.getX();
        this.motionY = (float) motion.getY();
        this.motionZ = (float) motion.getZ();
    }

    public void update() {
        float dpos = pow2(this.x - this.lastX) + pow2(this.y - this.lastY) + pow2(this.z - this.lastZ);
        float drot = pow2(this.yaw - this.lastYaw) + pow2(this.pitch - this.lastPitch);
        float dmotion = pow2(this.motionX - this.lastMotionX) + pow2(this.motionY - this.lastMotionY) + pow2(this.motionZ - this.lastMotionZ);
        if(dpos > .0001F || drot > 1F) {
            if(addMovement(this.x, this.y + this.entity.getBaseOffset(), this.z, this.yaw, this.pitch)) {
                this.lastX = x;
                this.lastY = y;
                this.lastZ = z;
                this.lastYaw = yaw;
                this.lastPitch = pitch;
            }
        }
        if(dmotion > .0025F || dmotion > .0001F && getMotion().lengthSquared() <= .0001F) {
            this.lastMotionX = this.motionX;
            this.lastMotionY = this.motionY;
            this.lastMotionZ = this.motionZ;
            addMotion(this.motionX, this.motionY, this.motionZ);
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public void refill(Location location) {
        location.setComponents(this.x, this.y, this.z);
        location.setYaw(this.yaw);
        location.setPitch(this.pitch);
    }

    protected boolean addMovement(float x, float y, float z, float yaw, float pitch) {
        EntityMoveEvent event = new EntityMoveEvent(this.entity, x, y, z, yaw, pitch);
        Lunaris.getInstance().getEventManager().call(event);
        if(event.isCancelled())
            return false;
        this.entity.getLocation().getChunk().sendPacket(new Packet12MoveEntity(this.entity.getEntityID(), x, y, z, yaw, pitch));
        return true;
    }

    protected void addMotion(float mx, float my, float mz) {
        this.entity.getLocation().getChunk().sendPacket(new Packet28SetEntityMotion(this.entity.getEntityID(), mx, my, mz));
    }

    protected Vector3f getMotion() {
        return new Vector3f(this.motionX, this.motionY, this.motionZ);
    }

    protected float pow2(float value) {
        return value * value;
    }

}
