package org.lunaris.nbt.tag;

import org.lunaris.nbt.stream.NBTInputStream;
import org.lunaris.nbt.stream.NBTOutputStream;

import java.io.IOException;
import java.util.Arrays;

public class ByteArrayTag extends Tag {
    public byte[] data;

    public ByteArrayTag(String name) {
        super(name);
    }

    public ByteArrayTag(String name, byte[] data) {
        super(name);
        this.data = data;
    }

    @Override
    void write(NBTOutputStream dos) throws IOException {
        if (data == null) {
            dos.writeInt(0);
            return;
        }
        dos.writeInt(data.length);
        dos.write(data);
    }

    @Override
    void load(NBTInputStream dis) throws IOException {
        int length = dis.readInt();
        data = new byte[length];
        dis.readFully(data);
    }

    @Override
    public byte getId() {
        return TAG_Byte_Array;
    }

    @Override
    public String toString() {
        return "ByteArrayTag " + this.getName() + " (data: 0x" + bytesToHexString(data, true) + " [" + data.length + " bytes])";
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            ByteArrayTag byteArrayTag = (ByteArrayTag) obj;
            return ((data == null && byteArrayTag.data == null) || (data != null && Arrays.equals(data, byteArrayTag.data)));
        }
        return false;
    }

    @Override
    public Tag copy() {
        byte[] cp = new byte[data.length];
        System.arraycopy(data, 0, cp, 0, data.length);
        return new ByteArrayTag(getName(), cp);
    }

    private String bytesToHexString(byte[] src, boolean blank) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }

        for (byte b : src) {
            if (!(stringBuilder.length() == 0) && blank) {
                stringBuilder.append(" ");
            }
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }

}
