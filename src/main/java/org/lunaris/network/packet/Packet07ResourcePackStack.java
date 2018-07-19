package org.lunaris.network.packet;

import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;
import org.lunaris.resourcepacks.ResourcePack;

/**
 * Created by RINES on 13.09.17.
 */
public class Packet07ResourcePackStack extends MinePacket {

    private boolean forced;
    private ResourcePack[] behaviourPackStack = new ResourcePack[0];
    private ResourcePack[] resourcePackStack = new ResourcePack[0];

    public Packet07ResourcePackStack() {}

    public Packet07ResourcePackStack(boolean forced, ResourcePack[] resourcePackStack) {
        this.forced = forced;
        this.resourcePackStack = resourcePackStack;
    }

    @Override
    public int getId() {
        return 0x07;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeBoolean(this.forced);
        buffer.writeUnsignedShort((short) this.behaviourPackStack.length);
        for(ResourcePack pack : this.behaviourPackStack) {
            buffer.writeString(pack.getPackId());
            buffer.writeString(pack.getPackVersion());
        }
        buffer.writeUnsignedShort((short) this.resourcePackStack.length);
        for(ResourcePack pack : this.resourcePackStack) {
            buffer.writeString(pack.getPackId());
            buffer.writeString(pack.getPackVersion());
        }
    }

    public boolean isForced() {
        return forced;
    }

    public ResourcePack[] getResourcePackStack() {
        return resourcePackStack;
    }

    public ResourcePack[] getBehaviourPackStack() {
        return resourcePackStack;
    }

}
