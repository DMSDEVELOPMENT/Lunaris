package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.entity.LEntity;
import org.lunaris.network.Packet;

/**
 * Created by RINES on 05.10.17.
 */
public class Packet1BEntityEvent extends Packet {

    private long entityID;
    private EntityEvent event;
    private int data;

    public Packet1BEntityEvent() {}

    public Packet1BEntityEvent(long entityID, EntityEvent event, int data) {
        this.entityID = entityID;
        this.event = event;
        this.data = data;
    }

    public Packet1BEntityEvent(LEntity entity, EntityEvent event) {
        this.entityID = entity.getEntityID();
        this.event = event;
    }

    @Override
    public byte getID() {
        return 0x1b;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.entityID = buffer.readUnsignedVarInt();
        byte eventID = buffer.readByte();
        for (EntityEvent event : EntityEvent.values())
            if (event.id == eventID) {
                this.event = event;
                break;
            }
        this.data = buffer.readSignedVarInt();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUnsignedVarLong(this.entityID);
        buffer.writeByte(this.event.id);
        buffer.writeSignedVarInt(this.data);
    }

    public enum EntityEvent {
        HURT_ANIMATION(2),
        DEATH_ANIMATION(3),
        TAME_FAIL(6),
        TAME_SUCCESS(7),
        SHAKE_WET(8),
        USE_ITEM(9),
        EAT_GRASS_ANIMATION(10),
        FISH_HOOK_BUBBLE(11),
        FISH_HOOK_POSITION(12),
        FISH_HOOK_HOOK(13),
        FISH_HOOK_TEASE(14),
        SQUID_INK_CLOUD(15),
        AMBIENT_SOUND(17),
        RESPAWN(18),
        ENCHANT(34),
        EATING_ITEM_ANIMATION(57);

        private final byte id;

        EntityEvent(int id) {
            this.id = (byte) id;
        }

    }

}
