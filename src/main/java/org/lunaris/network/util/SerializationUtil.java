package org.lunaris.network.util;

import io.gomint.jraknet.PacketBuffer;

import org.lunaris.api.item.ItemStack;
import org.lunaris.api.material.Material;
import org.lunaris.api.world.BlockFace;
import org.lunaris.api.world.Gamerule;
import org.lunaris.entity.data.*;
import org.lunaris.util.math.Vector3f;

import java.util.Map;

/**
 * @author xtrafrancyz
 */
public class SerializationUtil {
    private static final float BYTE_ROTATION_DIVIDOR = 360f / 256f;
    
    /**
     * Read a item stack from the packet buffer
     *
     * @param buffer from the packet
     * @return read item stack
     */
    public static ItemStack readItemStack(PacketBuffer buffer) {
        int id = buffer.readSignedVarInt();
        if (id <= 0)
            return ItemStack.AIR;
        int auxValue = buffer.readSignedVarInt();
        int data = auxValue >> 8;
        if (data == Short.MAX_VALUE)
            data = -1;
        int amount = auxValue & 0xff;
        int nbtLength = buffer.readLShort();
        if (nbtLength < 0)
            nbtLength = 0;
        byte[] nbt = new byte[nbtLength];
        buffer.readBytes(nbt);
        int canPlaceOnLength = buffer.readSignedVarInt();
        for (int i = 0; i < canPlaceOnLength; ++i)
            buffer.readString();
        int canDestroyLength = buffer.readSignedVarInt();
        for (int i = 0; i < canDestroyLength; ++i)
            buffer.readString();
        ItemStack is = new ItemStack(Material.getById(id), amount, data);
        is.setNbtData(nbt);
        return is;
    }

    /**
     * Write a item stack to the packet buffer
     *
     * @param is     which should be written
     * @param buffer which should be used to write to
     */
    public static void writeItemStack(ItemStack is, PacketBuffer buffer) {
        if (is == null || is.getType() == Material.AIR) {
            buffer.writeSignedVarInt(0);
            return;
        }
        buffer.writeSignedVarInt(is.getType().getId());
        int auxValue = (((is.getType().hasMeta() ? is.getData() : -1) & 0x7fff) << 8) | is.getAmount();
        buffer.writeSignedVarInt(auxValue);
        byte[] nbt = is.getNbtData();
        if (nbt == null) {
            buffer.writeLShort((short) 0);
        } else {
            buffer.writeLShort((short) nbt.length);
            buffer.writeBytes(nbt);
        }

        // canPlace and canBreak
        buffer.writeSignedVarInt(0);
        buffer.writeSignedVarInt(0);
    }

    /**
     * Write a array of item stacks to the buffer
     *
     * @param items  which should be written to the buffer
     * @param buffer which should be written to
     */
    public static void writeItemStacks(ItemStack[] items, PacketBuffer buffer) {
        if (items == null || items.length == 0) {
            buffer.writeUnsignedVarInt(0);
            return;
        }

        buffer.writeUnsignedVarInt(items.length);

        for (ItemStack itemStack : items)
            writeItemStack(itemStack, buffer);
    }

    /**
     * Read in a variable amount of itemstacks
     *
     * @param buffer The buffer to read from
     * @return a list of item stacks
     */
    public static ItemStack[] readItemStacks(PacketBuffer buffer) {
        int count = buffer.readUnsignedVarInt();
        ItemStack[] items = new ItemStack[count];

        for (int i = 0; i < count; i++)
            items[i] = readItemStack(buffer);

        return items;
    }

    /**
     * Write a array of integers to the buffer
     *
     * @param integers which should be written to the buffer
     * @param buffer   which should be written to
     */
    public static void writeIntList(int[] integers, PacketBuffer buffer) {
        if (integers == null || integers.length == 0) {
            buffer.writeUnsignedVarInt(0);
            return;
        }

        buffer.writeUnsignedVarInt(integers.length);

        for (Integer integer : integers) {
            buffer.writeSignedVarInt(integer);
        }
    }

    public static void writeGamerules(Map<Gamerule, Object> gamerules, PacketBuffer buffer) {
        if (gamerules == null) {
            buffer.writeUnsignedVarInt(0);
            return;
        }

        buffer.writeUnsignedVarInt(gamerules.size());
        gamerules.forEach((gamerule, value) -> {
            buffer.writeString(gamerule.getNbtName().toLowerCase());

            if (gamerule.getValueType() == Boolean.class) {
                buffer.writeByte((byte) 1);
                buffer.writeBoolean((Boolean) value);
            } else if (gamerule.getValueType() == Integer.class) {
                buffer.writeByte((byte) 2);
                buffer.writeUnsignedVarInt((Integer) value);
            } else if (gamerule.getValueType() == Float.class) {
                buffer.writeByte((byte) 3);
                buffer.writeLFloat((Float) value);
            }
        });
    }
    
    public static void writeMetadata(EntityMetadata metadata, PacketBuffer buffer) {
        Map<Integer, EntityData> map = metadata.getMap();
        buffer.writeUnsignedVarInt(map.size());
        map.forEach((id, data) -> {
            buffer.writeUnsignedVarInt(id);
            buffer.writeUnsignedVarInt(data.getTypeId());
            switch(data.getType()) {
                case BYTE:
                    buffer.writeByte(((ByteEntityData) data).getData().byteValue());
                    break;
                case SHORT:
                    buffer.writeLShort((short) (int) ((ShortEntityData) data).getData());
                    break;
                case INT:
                    buffer.writeSignedVarInt(((IntEntityData) data).getData());
                    break;
                case FLOAT:
                    buffer.writeLFloat(((FloatEntityData) data).getData());
                    break;
                case STRING:
                    buffer.writeString(((StringEntityData) data).getData());
                    break;
                case SLOT:
                    SlotEntityData slot = (SlotEntityData) data;
                    buffer.writeLShort((short) slot.blockId);
                    buffer.writeByte((byte) slot.meta);
                    buffer.writeLShort((short) slot.count);
                    break;
                case POS:
                    IntPositionEntityData pos = (IntPositionEntityData) data;
                    buffer.writeSignedVarInt(pos.x);
                    buffer.writeByte((byte) pos.y);
                    buffer.writeSignedVarInt(pos.z);
                    break;
                case LONG:
                    buffer.writeSignedVarLong(((LongEntityData) data).getData());
                    break;
                case VECTOR3F:
                    Vector3f vector = ((Vector3fEntityData) data).getData();
                    buffer.writeLFloat(vector.x);
                    buffer.writeLFloat(vector.y);
                    buffer.writeLFloat(vector.z);
                    break;
            }
        });
    }

    public static BlockFace readBlockFace(PacketBuffer buffer) {
        return BlockFace.fromIndex(buffer.readSignedVarInt());
    }

    public static void writeByteRotation(float rotation, PacketBuffer buffer) {
        buffer.writeByte((byte) (rotation / BYTE_ROTATION_DIVIDOR));
    }

    public static float readByteRotation(PacketBuffer buffer) {
        return buffer.readByte() * BYTE_ROTATION_DIVIDOR;
    }
}
