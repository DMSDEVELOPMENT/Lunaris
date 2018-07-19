package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;
import org.lunaris.resourcepacks.ResourcePack;

/**
 * Created by RINES on 13.09.17.
 */
public class Packet06ResourcePacksInfo extends Packet {

    private boolean forced;
    private ResourcePack[] behaviourPackEntries = new ResourcePack[0];
    private ResourcePack[] resourcePackEntries = new ResourcePack[0];

    @Override
    public byte getID() {
        return 0x06;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeBoolean(this.forced);
        buffer.writeLShort((short) this.behaviourPackEntries.length);
        for(ResourcePack pack : this.behaviourPackEntries) {
            buffer.writeString(pack.getPackId());
            buffer.writeString(pack.getPackVersion());
            buffer.writeLLong(pack.getPackSize());
            buffer.writeString(""); //unknown
            buffer.writeString("");
        }
        buffer.writeLShort((short) this.resourcePackEntries.length);
        for(ResourcePack pack : this.resourcePackEntries) {
            buffer.writeString(pack.getPackId());
            buffer.writeString(pack.getPackVersion());
            buffer.writeLLong(pack.getPackSize());
            buffer.writeString(""); //unknown
            buffer.writeString("");
        }
    }

    public boolean isForced() {
        return forced;
    }

    public ResourcePack[] getResourcePackEntries() {
        return resourcePackEntries;
    }

    public ResourcePack[] getBehaviourPackEntries() {
        return behaviourPackEntries;
    }

}
