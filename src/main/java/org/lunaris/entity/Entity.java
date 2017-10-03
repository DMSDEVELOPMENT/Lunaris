package org.lunaris.entity;

import org.lunaris.Lunaris;
import org.lunaris.block.Block;
import org.lunaris.entity.data.*;
import org.lunaris.event.entity.EntityDamageEvent;
import org.lunaris.network.protocol.packet.Packet27SetEntityData;
import org.lunaris.util.math.AxisAlignedBB;
import org.lunaris.util.math.LMath;
import org.lunaris.world.Location;
import org.lunaris.world.World;

import java.util.*;

/**
 * Created by RINES on 13.09.17.
 */
public class Entity extends Metadatable {

    private final long entityID;
    private final EntityMovement movement;
    private Location location;

    private final Map<Integer, Attribute> attributes = new HashMap<>();

    private int fireTicks;
    private int fallDistance;

    private AxisAlignedBB boundingBox;
    private Set<Block> blocksAround;
    private Set<Block> collisionBlocks;

    protected Entity(long entityID) {
        this.entityID = entityID;
        this.movement = generateEntityMovement();
    }

    @Override
    public long getEntityID() {
        return this.entityID;
    }

    public Attribute getAttribute(int id) {
        Attribute a = this.attributes.get(id);
        if(a != null)
            return a;
        a = Attribute.getAttribute(id);
        this.attributes.put(id, a);
        return a;
    }

    public void setAttribute(int id, float value) {
        Attribute a = getAttribute(id);
        a.setValue(value);
    }

    public Location getLocation() {
        return this.location;
    }

    public double getX() {
        return this.location.getX();
    }

    public double getY() {
        return this.location.getY();
    }

    public double getZ() {
        return this.location.getZ();
    }

    public World getWorld() {
        return this.location.getWorld();
    }

    public void initializeLocation(Location location) {
        if(this.location != null)
            throw new IllegalStateException("You can not use this method after entity's location has already been initialized.");
        this.location = location;
    }

    public void teleport(Location location) {
        this.movement.teleport(location);
        this.movement.update(); //notify players around old chunk
        this.location = location;
        this.movement.update(); //notify players around new chunk
    }

    public void moveTo(double x, double y, double z, double yaw, double pitch, double headYaw) {
        this.movement.setPositionAndRotation(x, y, z, yaw, pitch, headYaw);
    }

    public void setDisplayName(String name) {
        setDataProperty(EntityDataOption.NAMETAG, name);
    }

    public String getDisplayName() {
        return getDataPropertyString(EntityDataOption.NAMETAG);
    }

    public void setDisplayNameVisible(boolean visible, boolean always) {
        setDataFlag(false, EntityDataFlag.CAN_SHOW_NAMETAG, visible, false);
        setDataFlag(false, EntityDataFlag.ALWAYS_SHOW_NAMETAG, always, false);
    }

    public boolean isDisplayNameVisible() {
        return getDataFlag(false, EntityDataFlag.CAN_SHOW_NAMETAG);
    }

    public boolean isDisplayNameAlwaysVisible() {
        return isDisplayNameVisible() && getDataFlag(false, EntityDataFlag.ALWAYS_SHOW_NAMETAG);
    }

    public final void remove() {
        getWorld().removeEntityFromWorld(this);
    }

    public void tick() {
//        for(Block block : getCollisionBlocks()) {
//            block.getSpecifiedMaterial().onEntityCollide(block, this);
//        }
        if(this.fireTicks > 0) {
            //check fire resistance
            if(this instanceof LivingEntity)
                ((LivingEntity) this).damage(EntityDamageEvent.DamageCause.FIRE, 1);
            setDataFlag(false, EntityDataFlag.ON_FIRE, this.fireTicks --> 1, true);
        }
        if(isDirtyMetadata()) {
            setDirtyMetadata(false);
            Lunaris.getInstance().getNetworkManager().broadcastPacket(new Packet27SetEntityData(this.entityID, getDataProperties()));
        }
        this.movement.update();
        if(getY() <= -16) {
            if(this instanceof LivingEntity)
                ((LivingEntity) this).damage(EntityDamageEvent.DamageCause.VOID, 1);
            else
                remove();
        }
//        if(!(this instanceof Player))
//            recalculateCollisions();
    }

    public void recalculateCollisions() {
        this.blocksAround = this.collisionBlocks = null;
    }

    protected Set<Block> getCollisionBlocks() {
        if(this.collisionBlocks != null)
            return this.collisionBlocks;
        this.collisionBlocks = new HashSet<>();
        for(Block block : getBlocksAround())
            if(block.collidesWithBB(this.boundingBox, true))
                this.collisionBlocks.add(block);
        return this.collisionBlocks;
    }

    protected Set<Block> getBlocksAround() {
        if(this.blocksAround != null)
            return this.blocksAround;
        int minX = LMath.floorDouble(this.boundingBox.minX);
        int minY = LMath.floorDouble(this.boundingBox.minY);
        int minZ = LMath.floorDouble(this.boundingBox.minZ);
        int maxX = LMath.ceilDouble(this.boundingBox.maxX);
        int maxY = LMath.ceilDouble(this.boundingBox.maxY);
        int maxZ = LMath.ceilDouble(this.boundingBox.maxZ);
        final int X = this.location.getBlockX(), Y = this.location.getBlockY(), Z = this.location.getBlockZ();

        this.blocksAround = new HashSet<>();

        World world = getWorld();
        for (int z = minZ; z <= maxZ; ++z)
            for (int x = minX; x <= maxX; ++x)
                for (int y = minY; y <= maxY; ++y)
                    this.blocksAround.add(world.getBlockAt(X + x, Y + y, Z + z));
        return this.blocksAround;
    }

    public void setOnFire(int ticks) {
        this.fireTicks = ticks;
    }

    public void setFallDistance(int fallDistance) {
        this.fallDistance = fallDistance;
    }

    public int getFireTicks() {
        return this.fireTicks;
    }

    public int getFallDistance() {
        return fallDistance;
    }

    public float getEyeHeight() {
        return this.getHeight() / 2 + 0.1f;
    }

    public float getHeight() {
        return 0F;
    }

    public float getWidth() {
        return 0F;
    }

    public float getLength() {
        return 0F;
    }

    public float getBaseOffset() {
        return 0F;
    }

    protected EntityMovement generateEntityMovement() {
        return new EntityMovement(this);
    }

    @Override
    public int hashCode() {
        return (int) this.entityID;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(!(o instanceof Entity))
            return false;
        return this.entityID == ((Entity) o).entityID;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "-ID" + this.entityID;
    }

}
