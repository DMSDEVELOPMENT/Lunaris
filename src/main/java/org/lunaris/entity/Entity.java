package org.lunaris.entity;

import org.lunaris.Lunaris;
import org.lunaris.block.Block;
import org.lunaris.entity.data.*;
import org.lunaris.event.entity.EntityDamageEvent;
import org.lunaris.item.ItemStack;
import org.lunaris.network.protocol.packet.Packet27SetEntityData;
import org.lunaris.util.math.AxisAlignedBB;
import org.lunaris.util.math.LMath;
import org.lunaris.util.math.Vector3d;
import org.lunaris.world.Location;
import org.lunaris.world.World;

import java.util.*;

/**
 * Created by RINES on 13.09.17.
 */
public class Entity {

    private final long entityID;
    private Location location;

    private final Map<Integer, Attribute> attributes = new HashMap<>();

    private final EntityMetadata dataProperties = new EntityMetadata()
            .putLong(EntityDataOption.FLAGS, 0)
            .putShort(EntityDataOption.AIR, 400)
            .putShort(EntityDataOption.MAX_AIR, 400)
            .putString(EntityDataOption.NAMETAG, "")
            .putLong(EntityDataOption.LEAD_HOLDER_ENTITY_ID, -1)
            .putFloat(EntityDataOption.SCALE, 1f);

    private boolean dirtyMetadata = true;

    private int fireTicks;

    private AxisAlignedBB boundingBox;
    private Set<Block> blocksAround;
    private Set<Block> collisionBlocks;

    protected Entity(long entityID) {
        this.entityID = entityID;
    }

    public long getEntityID() {
        return entityID;
    }

    public boolean getDataFlag(boolean playerFlags, EntityDataFlag flag) {
        return ((playerFlags ? getDataPropertyByte(27) & 0xff : getDataPropertyLong(EntityDataOption.FLAGS)) & (1L << flag.ordinal())) > 0;
    }

    public void setDataFlag(boolean playerFlags, EntityDataFlag flag, boolean value, boolean send) {
        if(getDataFlag(playerFlags, flag) != value) {
            if(playerFlags) {
                byte flags = (byte) getDataPropertyByte(27);
                flags ^= 1 << flag.ordinal();
                setDataProperty(new ByteEntityData(27, flags), send);
            }else {
                long flags = getDataPropertyLong(EntityDataOption.FLAGS);
                flags ^= 1L << flag.ordinal();
                setDataProperty(new LongEntityData(0, flags), send);
            }
        }
    }

    public boolean setDataProperty(EntityData data, boolean send) {
        if(!Objects.equals(data, this.getDataProperties().get(data.getId()))) {
            this.getDataProperties().put(data);
            if(send) {
                EntityMetadata metadata = new EntityMetadata().put(this.dataProperties.get(data.getId()));
                Lunaris.getInstance().getNetworkManager().broadcastPacket(new Packet27SetEntityData(this.entityID, metadata));
            }else
                this.dirtyMetadata = true;
            return true;
        }
        return false;
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

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setDisplayName(String name) {
        setDataProperty(EntityDataOption.NAMETAG, name);
    }

    public String getDisplayName() {
        return getDataPropertyString(EntityDataOption.NAMETAG);
    }

    public void setDisplayNameVisible(boolean visible, boolean always) {
        setDataFlag(false, EntityDataFlag.CAN_SHOW_NAMETAG, visible, false);
        setDataFlag(false, EntityDataFlag.ALWAYS_SHOW_NAMETAG, always, true);
    }

    public EntityMetadata getDataProperties() {
        return this.dataProperties;
    }

    protected EntityData getDataProperty(EntityDataOption option) {
        return this.getDataProperties().get(option.ordinal());
    }

    protected int getDataPropertyInt(EntityDataOption option) {
        return this.getDataProperties().getInt(option.ordinal());
    }

    protected int getDataPropertyShort(EntityDataOption option) {
        return this.getDataProperties().getShort(option.ordinal());
    }

    protected int getDataPropertyByte(EntityDataOption option) {
        return this.getDataProperties().getByte(option.ordinal());
    }

    protected int getDataPropertyByte(int ordinal) {
        return this.getDataProperties().getByte(ordinal);
    }

    protected boolean getDataPropertyBoolean(EntityDataOption option) {
        return this.getDataProperties().getBoolean(option);
    }

    protected long getDataPropertyLong(EntityDataOption option) {
        return this.getDataProperties().getLong(option.ordinal());
    }

    protected String getDataPropertyString(EntityDataOption option) {
        return this.getDataProperties().getString(option);
    }

    protected float getDataPropertyFloat(EntityDataOption option) {
        return this.getDataProperties().getFloat(option);
    }

    protected ItemStack getDataPropertySlot(EntityDataOption option) {
        return this.getDataProperties().getSlot(option);
    }

    protected Vector3d getDataPropertyPos(EntityDataOption option) {
        return this.getDataProperties().getPosition(option);
    }

    protected void setDataProperty(EntityData data) {
        this.getDataProperties().put(data);
        this.dirtyMetadata = true;
    }

    protected void setDataProperty(EntityDataOption option, int value) {
        this.getDataProperties().putInt(option, value);
        this.dirtyMetadata = true;
    }

    protected void setDataProperty(EntityDataOption option, short value) {
        this.getDataProperties().putShort(option, value);
        this.dirtyMetadata = true;
    }

    protected void setDataProperty(EntityDataOption option, byte value) {
        this.getDataProperties().putByte(option, value);
        this.dirtyMetadata = true;
    }

    protected void setDataProperty(EntityDataOption option, boolean value) {
        this.getDataProperties().putBoolean(option, value);
        this.dirtyMetadata = true;
    }

    protected void setDataProperty(EntityDataOption option, long value) {
        this.getDataProperties().putLong(option, value);
        this.dirtyMetadata = true;
    }

    protected void setDataProperty(EntityDataOption option, String value) {
        this.getDataProperties().putString(option, value);
        this.dirtyMetadata = true;
    }

    protected void setDataProperty(EntityDataOption option, float value) {
        this.getDataProperties().putFloat(option, value);
        this.dirtyMetadata = true;
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
        if(this.dirtyMetadata) {
            this.dirtyMetadata = false;
            Lunaris.getInstance().getNetworkManager().broadcastPacket(new Packet27SetEntityData(this.entityID, getDataProperties()));
        }
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

    public boolean isDirtyMetadata() {
        return this.dirtyMetadata;
    }

    public void setDirtyMetadata(boolean dirtyMetadata) {
        this.dirtyMetadata = dirtyMetadata;
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
