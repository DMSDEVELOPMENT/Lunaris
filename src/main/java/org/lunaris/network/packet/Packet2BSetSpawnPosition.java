package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;

import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet2BSetSpawnPosition extends Packet {

    private SpawnType spawnType;
    private int x, y, z;
    private boolean spawnForced;

    public Packet2BSetSpawnPosition() {}

    public Packet2BSetSpawnPosition(SpawnType spawnType, int x, int y, int z, boolean spawnForced) {
        this.spawnType = spawnType;
        this.x = x;
        this.y = y;
        this.z = z;
        this.spawnForced = spawnForced;
    }

    @Override
    public byte getID() {
        return 0x2b;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeSignedVarInt(this.spawnType.ordinal());
        SerializationUtil.writeBlockVector(this.x, this.y, this.z, buffer);
        buffer.writeBoolean(this.spawnForced);
    }

    public enum SpawnType {
        PLAYER_SPAWN,
        WORLD_SPAWN
    }

}
