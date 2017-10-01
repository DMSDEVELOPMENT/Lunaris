package org.lunaris.network.protocol.packet;

import org.lunaris.block.Block;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 24.09.17.
 */
public class Packet15UpdateBlock extends MinePacket {

    private int x, y, z;
    private int id, data;

    public Packet15UpdateBlock(Block block) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.id = block.getTypeId();
        this.data = block.getData();
    }

    public Packet15UpdateBlock(int x, int y, int z, int id, int data) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.data = data;
    }

    @Override
    public int getId() {
        return 0x15;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeBlockVector(this.x, this.y, this.z);
        buffer.writeUnsignedVarInt(this.id);
        buffer.writeUnsignedVarInt((0xb << 4) | this.data & 0xf);
    }

}
