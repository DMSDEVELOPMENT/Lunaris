package org.lunaris.network.packet;

import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;
import org.lunaris.util.math.Vector3f;
import org.lunaris.api.world.Location;
import org.lunaris.api.world.Sound;

/**
 * Created by RINES on 28.09.17.
 */
public class Packet18LevelSoundEvent extends MinePacket {

    private Sound sound;
    private float x, y, z;
    private int extraData = -1, pitch = 1;
    private boolean isBabyMob, isGlobal;

    public Packet18LevelSoundEvent() {}

    public Packet18LevelSoundEvent(Sound sound, Location loc, int extraData, int pitch, boolean babyMob, boolean global) {
        this.sound = sound;
        this.x = (float) loc.getX();
        this.y = (float) loc.getY();
        this.z = (float) loc.getZ();
        this.extraData = extraData;
        this.pitch = pitch;
        this.isBabyMob = babyMob;
        this.isGlobal = global;
    }

    @Override
    public int getId() {
        return 0x18;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.sound = Sound.values()[buffer.readByte()];
        Vector3f v = buffer.readVector3f();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.extraData = buffer.readVarInt();
        this.pitch = buffer.readVarInt();
        this.isBabyMob = buffer.readBoolean();
        this.isGlobal = buffer.readBoolean();
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeByte((byte) this.sound.ordinal());
        buffer.writeVector3f(this.x, this.y, this.z);
        buffer.writeVarInt(this.extraData);
        buffer.writeVarInt(this.pitch);
        buffer.writeBoolean(this.isBabyMob);
        buffer.writeBoolean(this.isGlobal);
    }

}
