package org.lunaris.network.protocol.packet;

import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;
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
        buffer.writeLShort(this.behaviourPackEntries.length);
        for(ResourcePack pack : this.behaviourPackEntries) {
            buffer.writeString(pack.getPackId());
            buffer.writeString(pack.getPackVersion());
            buffer.writeLLong(pack.getPackSize());
            buffer.writeString(""); //unknown
        }
        buffer.writeLShort(this.resourcePackEntries.length);
        for(ResourcePack pack : this.resourcePackEntries) {
            buffer.writeString(pack.getPackId());
            buffer.writeString(pack.getPackVersion());
            buffer.writeLLong(pack.getPackSize());
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
