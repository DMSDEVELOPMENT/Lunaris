package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;
import org.lunaris.resourcepacks.ResourcePack;

/**
 * Created by RINES on 13.09.17.
 */
public class Packet07ResourcePackStack extends Packet {

    private boolean forced;
    private ResourcePack[] behaviourPackStack = new ResourcePack[0];
    private ResourcePack[] resourcePackStack = new ResourcePack[0];

    public Packet07ResourcePackStack() {}

    public Packet07ResourcePackStack(boolean forced, ResourcePack[] resourcePackStack) {
        this.forced = forced;
        this.resourcePackStack = resourcePackStack;
    }

    @Override
    public byte getID() {
        return 0x07;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeBoolean(this.forced);
        buffer.writeUnsignedVarInt(this.behaviourPackStack.length);
        for(ResourcePack pack : this.behaviourPackStack) {
            buffer.writeString(pack.getPackId());
            buffer.writeString(pack.getPackVersion());
            buffer.writeString(""); //???
        }
        buffer.writeUnsignedVarInt(this.resourcePackStack.length);
        for(ResourcePack pack : this.resourcePackStack) {
            buffer.writeString(pack.getPackId());
            buffer.writeString(pack.getPackVersion());
            buffer.writeString(""); //???
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
