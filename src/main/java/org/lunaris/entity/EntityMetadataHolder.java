package org.lunaris.entity;

import org.lunaris.api.item.ItemStack;
import org.lunaris.api.util.math.Vector3d;
import org.lunaris.entity.data.*;
import org.lunaris.network.packet.Packet27SetEntityData;

import java.lang.ref.WeakReference;
import java.util.Objects;

/**
 * Created by k.shandurenko on 21.07.2018
 */
public final class EntityMetadataHolder {

    private final WeakReference<LEntity> entityWeakReference;

    private final EntityMetadata dataProperties = new EntityMetadata()
            .putLong(EntityDataOption.FLAGS, 0)
            .putShort(EntityDataOption.AIR, 400)
            .putShort(EntityDataOption.MAX_AIR, 400)
            .putString(EntityDataOption.NAMETAG, "")
            .putLong(EntityDataOption.LEAD_HOLDER_ENTITY_ID, -1)
            .putFloat(EntityDataOption.SCALE, 1f);

    private boolean dirtyMetadata = true;

    EntityMetadataHolder(LEntity entity) {
        this.entityWeakReference = new WeakReference<>(entity);
    }

    public boolean getDataFlag(boolean playerFlags, EntityDataFlag flag) {
        return ((playerFlags ? getDataPropertyByte(27) & 0xff : getDataPropertyLong(EntityDataOption.FLAGS)) & (1L << flag.getID())) > 0;
    }

    public void setDataFlag(boolean playerFlags, EntityDataFlag flag, boolean value, boolean send) {
        if (getDataFlag(playerFlags, flag) != value) {
            if (playerFlags) {
                byte flags = (byte) getDataPropertyByte(27);
                flags ^= 1 << flag.getID();
                setDataProperty(new ByteEntityData(27, flags), send);
            } else {
                long flags = getDataPropertyLong(EntityDataOption.FLAGS);
                flags ^= 1L << flag.getID();
                setDataProperty(new LongEntityData(0, flags), send);
            }
        }
    }

    public boolean setDataProperty(EntityData data, boolean send) {
        if (!Objects.equals(data, this.getDataProperties().get(data.getId()))) {
            this.getDataProperties().put(data);
            if (send) {
                LEntity entity = getEntity();
                if (entity != null) {
                    EntityMetadata metadata = new EntityMetadata().put(this.dataProperties.get(data.getId()));
                    entity.sendPacketToWatchersAndMe(new Packet27SetEntityData(entity.getEntityID(), metadata));
                }
            } else {
                this.dirtyMetadata = true;
            }
            return true;
        }
        return false;
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

    private LEntity getEntity() {
        return this.entityWeakReference.get();
    }

    public boolean isDirtyMetadata() {
        return this.dirtyMetadata;
    }

    public void setDirtyMetadata(boolean dirtyMetadata) {
        this.dirtyMetadata = dirtyMetadata;
    }

}
