package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.network.Packet;

import java.util.Set;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet37AdventureSettings extends Packet {

    public static final int BITFLAG_SECOND_SET = 1 << 16;

    public long flags;
    public Packet4CAvailableCommands.CommandPermission commandPermissions = Packet4CAvailableCommands.CommandPermission.NORMAL;
    public long flags2;
    public long playerPermission = 1;
    public long customFlags;
    public long entityID;

    public Packet37AdventureSettings(long entityID, Set<Flag> setFlags) {
        setFlags.forEach(flag -> setFlag(flag, true));
        this.entityID = entityID;
    }

    public Packet37AdventureSettings() {}

    @Override
    public byte getID() {
        return 0x37;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.flags = buffer.readUnsignedVarLong();
        this.commandPermissions = Packet4CAvailableCommands.CommandPermission.values()[(int) buffer.readUnsignedVarLong()];
        this.flags2 = buffer.readUnsignedVarLong();
        this.playerPermission = buffer.readUnsignedVarLong();
        this.customFlags = buffer.readUnsignedVarLong();
        this.entityID = buffer.readLLong();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUnsignedVarLong(this.flags);
        buffer.writeUnsignedVarLong(this.commandPermissions.ordinal());
        buffer.writeUnsignedVarLong(this.flags2);
        buffer.writeUnsignedVarLong(this.playerPermission);
        buffer.writeUnsignedVarLong(this.customFlags);
        buffer.writeLLong(this.entityID);
    }

    public Packet37AdventureSettings flag(Flag flag, boolean setOrUnset) {
        setFlag(flag, setOrUnset);
        return this;
    }

    public void setFlag(Flag theFlag, boolean setOrUnset) {
        int flag = theFlag.mask;
        boolean flags = (flag & BITFLAG_SECOND_SET) != 0;
        if (setOrUnset) {
            if (flags) {
                this.flags2 |= flag;
            } else {
                this.flags |= flag;
            }
        } else if (flags) {
            this.flags2 &= ~flag;
        } else {
            this.flags &= ~flag;
        }
    }

    public enum Flag {
        WORLD_IMMUTABLE(0x01, false),
        NO_PVP(0x02, false),
        AUTO_JUMP(0x20, false),
        ALLOW_FLIGHT(0x40, false),
        NO_CLIP(0x80, false),
        WORLD_BUILDER(0x100, true),
        FLYING(0x200, false),
        MUTED(0x400, false),
        BUILD_AND_MINE(0x01 | BITFLAG_SECOND_SET, true),
        DOORS_AND_SWITCHES(0x02 | BITFLAG_SECOND_SET, true),
        OPEN_CONTAINERS(0x04 | BITFLAG_SECOND_SET, true),
        ATTACK_PLAYERS(0x08 | BITFLAG_SECOND_SET, true),
        ATTACK_MOBS(0x10 | BITFLAG_SECOND_SET, true),
        OPERATOR(0x20 | BITFLAG_SECOND_SET, false),
        TELEPORT(0x80 | BITFLAG_SECOND_SET, false);

        private final int mask;
        private boolean defaultValue;

        Flag(int mask, boolean defaultValue) {
            this.mask = mask;
            this.defaultValue = defaultValue;
        }

        public boolean hasDefaultValue() {
            return this.defaultValue;
        }

        public void setDefaultValue(boolean value) {
            this.defaultValue = value;
        }

    }

}
