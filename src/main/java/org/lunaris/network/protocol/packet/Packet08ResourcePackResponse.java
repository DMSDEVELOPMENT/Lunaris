package org.lunaris.network.protocol.packet;

import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 13.09.17.
 */
public class Packet08ResourcePackResponse extends MinePacket {

    public static final byte STATUS_REFUSED = 1;
    public static final byte STATUS_SEND_PACKS = 2;
    public static final byte STATUS_HAVE_ALL_PACKS = 3;
    public static final byte STATUS_COMPLETED = 4;

    private byte responseStatus;
    private String[] packIds;

    @Override
    public int getId() {
        return 0x08;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.responseStatus = buffer.readByte();
        this.packIds = new String[buffer.readLShort()];
        for(int i = 0; i < this.packIds.length; ++i)
            this.packIds[i] = buffer.readStringUnlimited();
    }

    @Override
    public void write(MineBuffer buffer) {

    }

    public byte getResponseStatus() {
        return responseStatus;
    }

    public String[] getPackIds() {
        return this.packIds;
    }

}
