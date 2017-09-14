package org.lunaris.network.protocol;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.lunaris.network.util.VarInt;
import org.lunaris.util.math.LMath;
import org.lunaris.util.math.Vector3f;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by RINES on 13.09.17.
 */
public class MineBuffer {

    private final ByteBuf buffer;

    public MineBuffer(ByteBuf buffer) {
        this.buffer = buffer;
    }

    public MineBuffer(int capacity) {
        this(Unpooled.buffer(capacity));
    }

    public void writeByte(byte value) {
        buffer.writeByte(value);
    }

    public byte readByte() {
        return buffer.readByte();
    }

    public void writeInt(int value) {
        buffer.writeInt(value);
    }

    public int readInt() {
        return buffer.readInt();
    }

    public int readUnsignedVarInt() {
        return readVarInt();
    }

    public void writeLInt(int value) {
        writeBytes(new byte[]{
                (byte) (value & 0xFF),
                (byte) ((value >>> 8) & 0xFF),
                (byte) ((value >>> 16) & 0xFF),
                (byte) ((value >>> 24) & 0xFF)
        });
    }

    public int readLInt() {
        byte[] bytes = readBytes(4);
        return ((bytes[3] & 0xff) << 24) +
                ((bytes[2] & 0xff) << 16) +
                ((bytes[1] & 0xff) << 8) +
                (bytes[0] & 0xff);
    }

    public void writeLShort(int value) {
        value &= 0xffff;
        writeByte((byte) (value & 0xFF));
        writeByte((byte) ((value >>> 8) & 0xFF));
    }

    public int readLShort() {
        return (readByte() & 0xFF) + ((byte) (readByte() & 0xFF) << 8);
    }

    public void writeLLong(long l) {
        writeBytes(new byte[]{
                (byte) (l),
                (byte) (l >>> 8),
                (byte) (l >>> 16),
                (byte) (l >>> 24),
                (byte) (l >>> 32),
                (byte) (l >>> 40),
                (byte) (l >>> 48),
                (byte) (l >>> 56),
        });
    }

    public long readLLong() {
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

    public void writeVarInt(int value) {
        while((value & -128) != 0) {
            this.writeByte((byte) (value & 127 | 128));
            value >>>= 7;
        }
        this.writeByte((byte) value);
    }

    public void writeVarIntLonger(long value) {
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

    public void writeVarLong(long value) {
        writeVarIntLonger(value);
    }

    public void putVarInt(int v) {
        writeVarIntLonger(VarInt.encodeZigZag32(v));
    }

    public int getVarInt() {
        return VarInt.decodeZigZag32(readVarInt());
    }

    public long getVarLong() {
        return VarInt.decodeZigZag64(readVarLong());
    }

    public void putVarLong(long v) {
        writeVarLong(VarInt.encodeZigZag64(v));
    }

    public void writeVector3f(float x, float y, float z) {
        writeLFloat(x);
        writeLFloat(y);
        writeLFloat(z);
    }

    public Vector3f getVector3f() {
        return new Vector3f(getLFloat(), getLFloat(), getLFloat());
    }

    public float getLFloat() {
        int accuracy = 4;
        float val = Float.intBitsToFloat(readLInt());
        if (accuracy > -1) {
            return (float) LMath.round(val, accuracy);
        } else {
            return val;
        }
    }

    public void writeBlockVector(int x, int y, int z) {
        this.putVarInt(x);
        this.writeVarInt(y);
        this.putVarInt(z);
    }

    public void writeLFloat(float v) {
        writeLInt(Float.floatToIntBits(v));
    }

    public int readVarInt() {
        int i = 0;
        int j = 0;
        while(true) {
            byte b0 = this.readByte();
            i |= (b0 & 127) << j++ * 7;
            if((b0 & 128) != 128)
                break;
        }
        return i;
    }

    public long readVarLong() {
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

    public void writeBytes(byte[] bytes) {
        buffer.writeBytes(bytes);
    }

    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        return bytes;
    }

    public void writeString(String value, int length) {
        if(value == null)
            value = "null";
        Preconditions.checkArgument(value.length() <= length, "String length exceeds %d (%s)", length, value);
        writeString(value);
    }

    public void writeString(String value) {
        if(value == null)
            value = "null";
        byte[] abyte = value.getBytes(Charsets.UTF_8);
        Preconditions.checkArgument(abyte.length <= 32767, "String is too big (was %d bytes encoded, whilst max is %d)", value.length(), 32767);
        this.writeVarInt(abyte.length);
        this.writeBytes(abyte);
    }

    public String readString(int maxPossibleLength) {
        int i = this.readVarInt();
        Preconditions.checkArgument(i <= maxPossibleLength << 2, "The received encoded string buffer length is longer than maximum allowed (%d > %d)", i, maxPossibleLength << 2);
        Preconditions.checkArgument(i >= 0, "The received encoded string buffer length is less than zero! What a weird string!");
        String s = new String(this.readBytes(i), Charsets.UTF_8);
        Preconditions.checkArgument(s.length() <= maxPossibleLength, "The received string length is longer than maximum allowed (%d > %d) ('%s')", i, maxPossibleLength, s);
        return s;
    }

    public void writeStringUnlimited(String value) {
        if(value == null)
            value = "null";
        int length = value.length();
        writeVarInt(length);
        writeString(value);
    }

    public void writeEnum(Enum theEnum) {
        writeByte((byte) theEnum.ordinal());
    }

    public void writeStringCollection(Collection<String> collection, int stringLimiter) {
        writeCollection(collection, s -> writeString(s, stringLimiter));
    }

    public <T> void writeCollection(Collection<T> collection, Consumer<T> writer) {
        writeVarInt(collection.size());
        collection.forEach(writer::accept);
    }

    public <S> List<S> readList(Supplier<S> reader) {
        return readCollection(new ArrayList<>(), reader);
    }

    public <S> Set<S> readSet(Supplier<S> reader) {
        return readCollection(new HashSet<>(), reader);
    }

    public <S, T extends Collection<S>> T readCollection(T collection, Supplier<S> reader) {
        int size = readVarInt();
        for(int i = 0; i < size; ++i)
            collection.add(reader.get());
        return collection;
    }

    public List<String> readStringList(int stringLimiter) {
        return readStringCollection(new ArrayList<>(), stringLimiter);
    }

    public Set<String> readStringSet(int stringLimiter) {
        return readStringCollection(new HashSet<>(), stringLimiter);
    }

    public <T extends Collection<String>> T readStringCollection(T collection, int stringLimiter) {
        return readCollection(collection, () -> readString(stringLimiter));
    }

    public <T extends Enum> T readEnum(Class<T> enumClass) {
        return enumClass.getEnumConstants()[readByte()];
    }

    public String readStringUnlimited() {
        return readString(readVarInt());
    }

    public void writeShort(short value) {
        buffer.writeShort(value);
    }

    public short readShort() {
        return buffer.readShort();
    }

    public void putUUID(UUID uuid) {
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

    public void writeBoolean(boolean value) {
        writeByte((byte) (value ? 1 : 0));
    }

    public boolean readBoolean() {
        return readByte() == 1;
    }

    public void writeLong(long value) {
        buffer.writeLong(value);
    }

    public long readLong() {
        return buffer.readLong();
    }

    public void writeFloat(float value) {
        buffer.writeFloat(value);
    }

    public float readFloat() {
        return buffer.readFloat();
    }

    public int remaining() {
        return buffer.readableBytes();
    }

    public int readableBytes() {
        return buffer.readableBytes();
    }

    public void free() {
        buffer.release();
    }

    public void release() {
        buffer.release();
    }

    public void skip(int bytes) {
        buffer.skipBytes(bytes);
    }

    public void skipBytes(int bytes) {
        skip(bytes);
    }

    public ByteBuf getBuffer() {
        return this.buffer;
    }

}
