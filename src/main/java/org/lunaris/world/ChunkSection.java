package org.lunaris.world;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by RINES on 13.09.17.
 */
public class ChunkSection {

    private final byte[] ids;
    private final byte[] datas;
    private boolean empty;

    ChunkSection() {
        this(new byte[1 << 13], new byte[1 << 12], true);
        Arrays.fill(this.datas, (byte) 0);
    }

    ChunkSection(byte[] ids, byte[] datas, boolean empty) {
        this.ids = ids;
        this.datas = datas;
        this.empty = empty;
    }

    short getId(int x, int y, int z) {
        int key = i(x, y, z);
        byte right = this.ids[key << 1];
        byte left = this.ids[(key << 1) + 1];
        return (short) ((right & 0xff) + ((byte) (left & 0xFF) << 8));
    }

    byte getData(int x, int y, int z) {
        return this.datas[i(x, y, z)];
    }

    void set(int x, int y, int z, short id, byte data) {
        if(id > 0)
            this.empty = false;
        int i = i(x, y, z);
        this.ids[i << 1] = (byte) (id & 0xff);
        this.ids[(i << 1) + 1] = (byte) ((id >>> 8) & 0xff);
        this.datas[i] = data;
    }

    boolean isEmpty() {
        return this.empty;
    }

    byte[] getBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(6144);
        byte[] blocks = new byte[4096];
        byte[] data = new byte[2048];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int i = (x << 7) | (z << 3);
                for (int y = 0; y < 16; y += 2) {
                    blocks[(i << 1) | y] = (byte) getId(x, y, z);
                    blocks[(i << 1) | (y + 1)] = (byte) getId(x, y + 1, z);
                    int b1 = getData(x, y, z);
                    int b2 = getData(x, y + 1, z);
                    data[i | (y >> 1)] = (byte) ((b2 << 4) | b1);
                }
            }
        }
        return buffer.put(blocks).put(data).array();
    }

    byte[] getColumn(int x, int z) {
        x &= 15; z &= 15;
        byte[] column = new byte[1 << 5];
        for(int y = 0; y < 16; ++y) {
            int i = y << 8 | z << 4 | x;
            column[y << 1] = this.ids[i << 1];
            column[(y << 1) + 1] = this.ids[(i << 1) + 1];
        }
        return column;
    }

    private int i(int x, int y, int z) {
        x &= 0xf; y &= 0xf; z &= 0xf;
        return (y << 8) | (z << 4) | x;
    }

}
