package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;

import org.lunaris.entity.LPlayer;
import org.lunaris.entity.misc.Skin;
import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;

import java.util.UUID;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet3FPlayerList extends Packet {

    public Type type;
    public Entry[] entries = new Entry[0];

    public Packet3FPlayerList() {}

    public Packet3FPlayerList(Type type, Entry... entries) {
        this.type = type;
        this.entries = entries;
    }

    @Override
    public byte getID() {
        return 0x3f;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeByte((byte) this.type.ordinal());
        buffer.writeUnsignedVarInt(this.entries.length);
        for (Entry e : this.entries) {
            if (this.type == Type.ADD) {
                buffer.writeUUID(e.uuid);
                buffer.writeSignedVarLong(e.entityId);
                buffer.writeString(e.name);

                buffer.writeString(e.name); // third party name
                buffer.writeSignedVarInt(0xFFFFFFFF); // platform id

                buffer.writeString(e.skin.getModel());
                SerializationUtil.writeByteArray(e.skin.getData(), buffer);
                SerializationUtil.writeByteArray(e.skin.getCape().getData(), buffer);

                buffer.writeString(e.geometryModel);
                SerializationUtil.writeByteArray(e.geometryData, buffer);

                buffer.writeString(e.xboxUserId);

                buffer.writeString(e.uuid.toString()); // platformChatId ??
            } else {
                buffer.writeUUID(e.uuid);
            }
        }
    }

    public enum Type {
        ADD, REMOVE
    }

    public static class Entry {

        public final UUID uuid;
        public long entityId = 0;
        public String name = "";
        public Skin skin;
        public String geometryModel = "";
        public byte[] geometryData = new byte[0];
        public String xboxUserId = "";

        public Entry(UUID uuid) {
            this.uuid = uuid;
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin, String xboxID) {
            this.uuid = uuid;
            this.entityId = entityId;
            this.name = name;
            this.skin = skin;
            this.xboxUserId = xboxID;
        }

        public Entry(LPlayer player) {
            this(player.getUUID(), player.getEntityID(), player.getName(), player.getSkin(), player.getXboxID());
        }

    }

}
