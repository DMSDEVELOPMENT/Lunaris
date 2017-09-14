package org.lunaris.network.protocol.packet;

import org.lunaris.entity.Player;
import org.lunaris.entity.data.Skin;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

import java.util.UUID;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet3FPlayerList extends MinePacket {

    public Type type;
    public Entry[] entries = new Entry[0];

    public Packet3FPlayerList() {}

    public Packet3FPlayerList(Type type, Entry[] entries) {
        this.type = type;
        this.entries = entries;
    }

    @Override
    public int getId() {
        return 0x3f;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeByte((byte) this.type.ordinal());
        buffer.writeVarIntLonger(this.entries.length);
        for(Entry e : this.entries) {
            if(this.type == Type.ADD) {
                buffer.putUUID(e.uuid);
                buffer.putVarLong(e.entityId);
                buffer.writeStringUnlimited(e.name);
                buffer.writeStringUnlimited(e.skin.getModel());
                buffer.writeVarIntLonger(e.skin.getData().length);
                buffer.writeBytes(e.skin.getData());
            }else {
                buffer.putUUID(e.uuid);
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

        public Entry(UUID uuid) {
            this.uuid = uuid;
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin) {
            this.uuid = uuid;
            this.entityId = entityId;
            this.name = name;
            this.skin = skin;
        }

        public Entry(Player player) {
            this(player.getClientUUID(), player.getEntityID(), player.getName(), player.getSkin());
        }

    }

}
