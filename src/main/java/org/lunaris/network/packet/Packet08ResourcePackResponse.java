package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 13.09.17.
 */
public class Packet08ResourcePackResponse extends Packet {

    public static final byte STATUS_REFUSED = 1;
    public static final byte STATUS_SEND_PACKS = 2;
    public static final byte STATUS_HAVE_ALL_PACKS = 3;
    public static final byte STATUS_COMPLETED = 4;

    private byte responseStatus;
    private String[] packIds;
    private String[] packNames;

    @Override
    public byte getID() {
        return 0x08;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.responseStatus = buffer.readByte();
        this.packIds = new String[buffer.readLShort()];
        this.packNames = new String[this.packIds.length];
        for(int i = 0; i < this.packIds.length; ++i) {
            this.packIds[i] = buffer.readString();
            this.packNames[i] = buffer.readString();
        }
    }

    @Override
    public void write(PacketBuffer buffer) {

    }

    public byte getResponseStatus() {
        return responseStatus;
    }

    public String[] getPackIds() {
        return this.packIds;
    }

    public String[] getPackNames() {
        return this.packNames;
    }

}
