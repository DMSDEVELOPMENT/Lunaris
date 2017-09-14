package org.lunaris.entity.data;

import org.lunaris.block.Material;
import org.lunaris.item.ItemStack;
import org.lunaris.util.math.Vector3d;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by RINES on 14.09.17.
 */
public class EntityMetadata {

    private final Map<Integer, EntityData> map = new HashMap<>();

    public EntityData get(int id) {
        return this.getOrDefault(id, null);
    }

    public EntityData getOrDefault(int id, EntityData defaultValue) {
        try {
            return this.map.getOrDefault(id, defaultValue).setId(id);
        } catch (Exception e) {
            if (defaultValue != null) {
                return defaultValue.setId(id);
            }
            return null;
        }
    }

    public boolean exists(int id) {
        return this.map.containsKey(id);
    }

    public EntityMetadata put(EntityData data) {
        this.map.put(data.getId(), data);
        return this;
    }

    public int getByte(int id) {
        return (int) this.getOrDefault(id, new ByteEntityData(id, 0)).getData() & 0xff;
    }

    public int getShort(int id) {
        return (int) this.getOrDefault(id, new ShortEntityData(id, 0)).getData();
    }

    public int getInt(int id) {
        return (int) this.getOrDefault(id, new IntEntityData(id, 0)).getData();
    }

    public long getLong(int id) {
        return (Long) this.getOrDefault(id, new LongEntityData(id, 0)).getData();
    }

    public float getFloat(EntityDataOption option) {
        return (float) this.getOrDefault(option.ordinal(), new FloatEntityData(option.ordinal(), 0)).getData();
    }

    public boolean getBoolean(EntityDataOption option) {
        return this.getByte(option.ordinal()) == 1;
    }

    public ItemStack getSlot(EntityDataOption option) {
        return (ItemStack) this.getOrDefault(option.ordinal(), new SlotEntityData(option.ordinal(), new ItemStack(Material.AIR))).getData();
    }

    public String getString(EntityDataOption option) {
        return (String) this.getOrDefault(option.ordinal(), new StringEntityData(option.ordinal(), "")).getData();
    }

    public Vector3d getPosition(EntityDataOption option) {
        return (Vector3d) this.getOrDefault(option.ordinal(), new IntPositionEntityData(option.ordinal(), new Vector3d())).getData();
    }

    public EntityMetadata putByte(EntityDataOption option, int value) {
        return this.put(new ByteEntityData(option.ordinal(), value));
    }

    public EntityMetadata putShort(EntityDataOption type, int value) {
        return this.put(new ShortEntityData(type.ordinal(), value));
    }

    public EntityMetadata putInt(EntityDataOption type, int value) {
        return this.put(new IntEntityData(type.ordinal(), value));
    }

    public EntityMetadata putLong(EntityDataOption type, long value) {
        return this.put(new LongEntityData(type.ordinal(), value));
    }

    public EntityMetadata putFloat(EntityDataOption option, float value) {
        return this.put(new FloatEntityData(option.ordinal(), value));
    }

    public EntityMetadata putBoolean(EntityDataOption option, boolean value) {
        return this.putByte(option, value ? 1 : 0);
    }

    public EntityMetadata putSlot(EntityDataOption option, int blockId, int meta, int count) {
        return this.put(new SlotEntityData(option.ordinal(), blockId, (byte) meta, count));
    }

    public EntityMetadata putSlot(EntityDataOption option, ItemStack value) {
        return this.put(new SlotEntityData(option.ordinal(), value));
    }

    public EntityMetadata putString(EntityDataOption option, String value) {
        return this.put(new StringEntityData(option.ordinal(), value));
    }

    public Map<Integer, EntityData> getMap() {
        return new HashMap<>(map);
    }
    
}
