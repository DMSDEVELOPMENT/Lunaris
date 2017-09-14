package org.lunaris.entity;

import org.lunaris.Lunaris;
import org.lunaris.entity.data.*;
import org.lunaris.item.ItemStack;
import org.lunaris.network.protocol.packet.Packet27SetEntityData;
import org.lunaris.util.math.Vector3d;
import org.lunaris.world.Location;
import org.lunaris.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
                Lunaris.getInstance().getNetworkManager().sendPacket(getWorld().getApplicablePlayers(this.location), new Packet27SetEntityData(this.entityID, metadata));
            }
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

    public World getWorld() {
        return this.location.getWorld();
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public EntityMetadata getDataProperties() {
        return this.dataProperties;
    }

    public EntityData getDataProperty(EntityDataOption option) {
        return this.getDataProperties().get(option.ordinal());
    }

    public int getDataPropertyInt(EntityDataOption option) {
        return this.getDataProperties().getInt(option.ordinal());
    }

    public int getDataPropertyShort(EntityDataOption option) {
        return this.getDataProperties().getShort(option.ordinal());
    }

    public int getDataPropertyByte(EntityDataOption option) {
        return this.getDataProperties().getByte(option.ordinal());
    }

    public int getDataPropertyByte(int ordinal) {
        return this.getDataProperties().getByte(ordinal);
    }

    public boolean getDataPropertyBoolean(EntityDataOption option) {
        return this.getDataProperties().getBoolean(option);
    }

    public long getDataPropertyLong(EntityDataOption option) {
        return this.getDataProperties().getLong(option.ordinal());
    }

    public String getDataPropertyString(EntityDataOption option) {
        return this.getDataProperties().getString(option);
    }

    public float getDataPropertyFloat(EntityDataOption option) {
        return this.getDataProperties().getFloat(option);
    }

    public ItemStack getDataPropertySlot(EntityDataOption option) {
        return this.getDataProperties().getSlot(option);
    }

    public Vector3d getDataPropertyPos(EntityDataOption option) {
        return this.getDataProperties().getPosition(option);
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
