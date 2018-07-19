package org.lunaris.network_old.protocol.packet;

import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet2BSetSpawnPosition extends MinePacket {

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
    public int getId() {
        return 0x2b;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeVarInt(this.spawnType.ordinal());
        buffer.writeBlockVector(this.x, this.y, this.z);
        buffer.writeBoolean(this.spawnForced);
    }

    public enum SpawnType {
        PLAYER_SPAWN,
        WORLD_SPAWN
    }

}
