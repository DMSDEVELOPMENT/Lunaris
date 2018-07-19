package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;
import org.lunaris.util.math.Vector3f;
import org.lunaris.api.world.Location;
import org.lunaris.api.world.Sound;

/**
 * Created by RINES on 28.09.17.
 */
public class Packet18LevelSoundEvent extends Packet {

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
    public byte getID() {
        return 0x18;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.sound = Sound.values()[buffer.readByte()];
        Vector3f v = SerializationUtil.readVector3f(buffer);
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.extraData = buffer.readSignedVarInt();
        this.pitch = buffer.readSignedVarInt();
        this.isBabyMob = buffer.readBoolean();
        this.isGlobal = buffer.readBoolean();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeByte((byte) this.sound.ordinal());
        SerializationUtil.writeVector3f(new Vector3f(this.x, this.y, this.z), buffer);
        buffer.writeSignedVarInt(this.extraData);
        buffer.writeSignedVarInt(this.pitch);
        buffer.writeBoolean(this.isBabyMob);
        buffer.writeBoolean(this.isGlobal);
    }

}
