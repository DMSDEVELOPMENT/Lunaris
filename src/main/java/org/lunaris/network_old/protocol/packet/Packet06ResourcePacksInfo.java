package org.lunaris.network_old.protocol.packet;

import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;
import org.lunaris.resourcepacks.ResourcePack;

/**
 * Created by RINES on 13.09.17.
 */
public class Packet06ResourcePacksInfo extends MinePacket {

    private boolean forced;
    private ResourcePack[] behaviourPackEntries = new ResourcePack[0];
    private ResourcePack[] resourcePackEntries = new ResourcePack[0];

    @Override
    public int getId() {
        return 0x06;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeBoolean(this.forced);
        buffer.writeUnsignedShort((short) this.behaviourPackEntries.length);
        for(ResourcePack pack : this.behaviourPackEntries) {
            buffer.writeString(pack.getPackId());
            buffer.writeString(pack.getPackVersion());
            buffer.writeUnsignedLong(pack.getPackSize());
            buffer.writeString(""); //unknown
        }
        buffer.writeUnsignedShort((short) this.resourcePackEntries.length);
        for(ResourcePack pack : this.resourcePackEntries) {
            buffer.writeString(pack.getPackId());
            buffer.writeString(pack.getPackVersion());
            buffer.writeUnsignedLong(pack.getPackSize());
            buffer.writeString(""); //unknown
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
