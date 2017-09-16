package org.lunaris.network.protocol;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.lunaris.block.Material;
import org.lunaris.entity.data.*;
import org.lunaris.item.ItemStack;
import org.lunaris.util.math.Vector3d;
import org.lunaris.util.math.Vector3f;
import org.lunaris.world.BlockVector;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

/**
 * Created by RINES on 16.09.17.
 */
public class MineBuffer {

    private final ByteBuf buffer;

    public MineBuffer(ByteBuf buffer) {
        this.buffer = buffer;
    }

    public MineBuffer(int capacity) {
        this(Unpooled.buffer(capacity));
    }

    public int remaining() {
        return this.buffer.readableBytes();
    }

    public int readableBytes() {
        return remaining();
    }

    public void free() {
        this.buffer.release();
    }

    public void release() {
        free();
    }

    public void skip(int bytes) {
        this.buffer.skipBytes(bytes);
    }

    public void skipBytes(int bytes) {
        skip(bytes);
    }

    public ByteBuf getBuffer() {
        return this.buffer;
    }

    public void writeByte(byte value) {
        this.buffer.writeByte(value);
    }

    public byte readByte() {
        return this.buffer.readByte();
    }

    public void writeInt(int value) {
        this.buffer.writeInt(value);
    }

    public int readInt() {
        return this.buffer.readInt();
    }

    public void writeBytes(byte[] bytes) {
        this.buffer.writeBytes(bytes);
    }

    public byte[] readBytes(int length) {
        length = Math.min(length, remaining());
        if(length <= 0)
            return new byte[0];
        byte[] bytes = new byte[length];
        this.buffer.readBytes(bytes);
        return bytes;
    }

    public void writeUnsignedVarInt(int value) {
        while((value & -128) != 0) {
            this.writeByte((byte) (value & 127 | 128));
            value >>>= 7;
        }
        this.writeByte((byte) value);
    }

    public int readUnsignedVarInt() {
        long value = 0;
        int size = 0;
        int b;
        while (((b = readByte()) & 0x80) == 0x80) {
            value |= (long) (b & 0x7F) << (size++ * 7);
            if (size >= 5) {
                throw new IllegalArgumentException("VarInt too big");
            }
        }

        return (int) (value | ((long) (b & 0x7F) << (size * 7)));
    }

    public void writeUnsignedVarLong(long value) {
        do {
            byte temp = (byte)(value & 0b01111111);
            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                temp |= 0b10000000;
            }
            writeByte(temp);
        } while (value != 0);
    }

    public long readUnsignedVarLong() {
        long value = 0;
        int size = 0;
        int b;
        while (((b = readByte()) & 0x80) == 0x80) {
            value |= (long) (b & 0x7F) << (size++ * 7);
            if (size >= 10) {
                throw new IllegalArgumentException("VarLong too big");
            }
        }
        return value | ((long) (b & 0x7F) << (size * 7));
    }

    public void writeVarInt(int value) {
        writeUnsignedVarLong(encodeZigZag32(value));
    }

    public int readVarInt() {
        return decodeZigZag32(readUnsignedVarInt());
    }

    public void writeVarLong(long value) {
        writeUnsignedVarLong(encodeZigZag64(value));
    }

    public long readVarLong() {
        return decodeZigZag64(readUnsignedVarLong());
    }

    public static long encodeZigZag32(int v) {
        return (long) ((v << 1) ^ (v >> 31));
    }

    public static int decodeZigZag32(long v) {
        return (int) (v >> 1) ^ -(int) (v & 1);
    }

    public static long encodeZigZag64(long v) {
        return (v << 1) ^ (v >> 63);
    }

    public static long decodeZigZag64(long v) {
        return (v >>> 1) ^ -(v & 1);
    }

    public void writeEntityRuntimeId(long id) {
        writeVarLong(id);
    }

    public long readEntityRuntimeId() {
        return readVarLong();
    }

    public void writeBoolean(boolean value) {
        this.buffer.writeBoolean(value);
    }

    public boolean readBoolean() {
        return this.buffer.readBoolean();
    }

    public void writeUnsignedInt(int value) {
        writeBytes(new byte[]{
                (byte) (value & 0xFF),
                (byte) ((value >>> 8) & 0xFF),
                (byte) ((value >>> 16) & 0xFF),
                (byte) ((value >>> 24) & 0xFF)
        });
    }

    public int readUnsignedInt() {
        byte[] bytes = readBytes(4);
        return ((bytes[3] & 0xff) << 24) +
                ((bytes[2] & 0xff) << 16) +
                ((bytes[1] & 0xff) << 8) +
                (bytes[0] & 0xff);
    }

    public void writeUnsignedLong(long l) {
        writeBytes(lfu(l));
    }

    public long readUnsignedLong() {
        byte[] bytes = readBytes(8);
        return (((long) bytes[7] << 56) +
                ((long) (bytes[6] & 0xFF) << 48) +
                ((long) (bytes[5] & 0xFF) << 40) +
                ((long) (bytes[4] & 0xFF) << 32) +
                ((long) (bytes[3] & 0xFF) << 24) +
                ((bytes[2] & 0xFF) << 16) +
                ((bytes[1] & 0xFF) << 8) +
                ((bytes[0] & 0xFF)));
    }

    public void writeUnsignedShort(short s) {
        s &= 0xffff;
        writeBytes(new byte[]{
                (byte) (s & 0xFF),
                (byte) ((s >>> 8) & 0xFF)
        });
    }

    public short readUnsignedShort() {
        return (short) ((readByte() & 0xFF) + (readByte() & 0xFF << 8));
    }

    public void writeFloat(float value) {
        writeUnsignedInt(Float.floatToIntBits(value));
    }

    public float readFloat() {
        return Float.intBitsToFloat(readUnsignedInt());
    }

    public void writeByteArray(byte[] bytes) {
        writeUnsignedVarInt(bytes.length);
        writeBytes(bytes);
    }

    public byte[] readByteArray() {
        byte[] bytes = new byte[readUnsignedVarInt()];
        this.buffer.readBytes(bytes);
        return bytes;
    }

    public void writeString(String value) {
        if(value == null)
            value = "null";
        byte[] abyte = value.getBytes(Charsets.UTF_8);
        Preconditions.checkArgument(abyte.length <= 32767, "String is too big (was %d bytes encoded, whilst max is %d)", value.length(), 32767);
        this.writeUnsignedVarInt(abyte.length);
        this.writeBytes(abyte);
    }

    public String readString() {
        String s = new String(this.readBytes(readUnsignedVarInt()), Charsets.UTF_8);
        return s;
    }

    public void writeVector3d(Vector3d vector) {
        writeVector3f((float) vector.x, (float) vector.y, (float) vector.z);
    }

    public void writeVector3f(Vector3f vector) {
        writeVector3f(vector.x, vector.y, vector.z);
    }

    public void writeVector3f(float x, float y, float z) {
        writeFloat(x);
        writeFloat(y);
        writeFloat(z);
    }

    public Vector3f readVector3f() {
        return new Vector3f(readFloat(), readFloat(), readFloat());
    }

    public Vector3d readVector3d() {
        return new Vector3d(readFloat(), readFloat(), readFloat());
    }

    public void writeVector2f(float x, float y) {
        writeFloat(x);
        writeFloat(y);
    }

    public void writeBlockVector(BlockVector vector) {
        writeBlockVector(vector.x, vector.y, vector.z);
    }

    public void writeBlockVector(int x, int y, int z) {
        writeVarInt(x);
        writeUnsignedVarInt(y);
        writeVarInt(z);
    }

    public BlockVector readBlockVector() {
        return new BlockVector(readVarInt(), readUnsignedVarInt(), readVarInt());
    }

    public void writeMetadata(EntityMetadata metadata) {
        Map<Integer, EntityData> map = metadata.getMap();
        writeUnsignedVarInt(map.size());
        map.forEach((id, data) -> {
            writeUnsignedVarInt(id);
            writeUnsignedVarInt(data.getTypeId());
            switch(data.getType()) {
                case BYTE:
                    writeByte(((ByteEntityData) data).getData().byteValue());
                    break;
                case SHORT:
                    writeUnsignedShort((short) (int) ((ShortEntityData) data).getData());
                    break;
                case INT:
                    writeVarInt(((IntEntityData) data).getData());
                    break;
                case FLOAT:
                    writeFloat(((FloatEntityData) data).getData());
                    break;
                case STRING:
                    writeString(((StringEntityData) data).getData());
                    break;
                case SLOT:
                    SlotEntityData slot = (SlotEntityData) data;
                    writeUnsignedShort((short) slot.blockId);
                    writeByte((byte) slot.meta);
                    writeUnsignedShort((short) slot.count);
                    break;
                case POS:
                    IntPositionEntityData pos = (IntPositionEntityData) data;
                    writeVarInt(pos.x);
                    writeByte((byte) pos.y);
                    writeVarInt(pos.z);
                    break;
                case LONG:
                    writeVarLong(((LongEntityData) data).getData());
                    break;
                case VECTOR3F:
                    Vector3f vector = ((Vector3fEntityData) data).getData();
                    writeVector3f(vector.x, vector.y, vector.z);
                    break;
            }
        });
    }

    public void writeItem(ItemStack item) {
        if (item == null || item.getMaterial() == Material.AIR) {
            writeVarInt(0);
            return;
        }
        writeVarInt(item.getMaterial().getId());
        int auxValue = (((item.getMaterial().hasMeta() ? item.getData() : -1) & 0x7fff) << 8) | item.getAmount();
        writeVarInt(auxValue);
//        byte[] nbt = item.getCompoundTag();
//        this.putLShort(nbt.length);
//        this.put(nbt);
        writeUnsignedShort((short) 0);
        writeVarInt(0); //can place on entry amount
        writeVarInt(0); //can destroy entry amount
    }

    public void writeUUID(UUID uuid) {
        writeBytes(appendBytes(lfu(uuid.getMostSignificantBits()), lfu(uuid.getLeastSignificantBits())));
    }

    public byte[] appendBytes(byte[] bytes1, byte[]... bytes2) {
        int length = bytes1.length;
        for (byte[] bytes : bytes2) {
            length += bytes.length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.put(bytes1);
        for (byte[] bytes : bytes2) {
            buffer.put(bytes);
        }
        return buffer.array();
    }

    private byte[] lfu(long l) {
        return new byte[]{
                (byte) (l),
                (byte) (l >>> 8),
                (byte) (l >>> 16),
                (byte) (l >>> 24),
                (byte) (l >>> 32),
                (byte) (l >>> 40),
                (byte) (l >>> 48),
                (byte) (l >>> 56),
        };
    }

}
