package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.block.LBlock;
import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;
import org.lunaris.world.BlockVector;

/**
 * Created by RINES on 24.09.17.
 */
//TODO: отличается от мяты
public class Packet15UpdateBlock extends Packet {

    public static final int FLAG_NONE = 0b0000;
    public static final int FLAG_NEIGHBORS = 0b0001;
    public static final int FLAG_NETWORK = 0b0010;
    public static final int FLAG_NOGRAPHIC = 0b0100;
    public static final int FLAG_PRIORITY = 0b1000;
    public static final int FLAG_ALL = FLAG_NEIGHBORS | FLAG_NETWORK;
    public static final int FLAG_ALL_PRIORITY = FLAG_ALL | FLAG_PRIORITY;

    private int x, y, z;
    private int id, flag, data;

    public Packet15UpdateBlock() {}

    public Packet15UpdateBlock(LBlock block) {
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.id = block.getTypeId();
        this.data = block.getData();
    }

    public Packet15UpdateBlock(int x, int y, int z, int id, int data) {
        this(x, y, z, id, FLAG_ALL, data);
    }

    public Packet15UpdateBlock(int x, int y, int z, int id, int flag, int data) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.flag = flag;
        this.data = data;
    }

    @Override
    public byte getID() {
        return 0x15;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        SerializationUtil.writeBlockVector(new BlockVector(this.x, this.y, this.z), buffer);
        buffer.writeUnsignedVarInt(this.id);
        buffer.writeUnsignedVarInt(this.flag);
        buffer.writeUnsignedVarInt((0xb << 4) | this.data & 0xf);
    }

}
