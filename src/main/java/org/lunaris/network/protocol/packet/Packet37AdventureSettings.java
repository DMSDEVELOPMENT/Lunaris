package org.lunaris.network.protocol.packet;

import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

import java.util.Set;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet37AdventureSettings extends MinePacket {

    public static final int BITFLAG_SECOND_SET = 1 << 16;

    public long flags;
    public CommandPermissionLevel commandPermissions = CommandPermissionLevel.NORMAL;
    public long flags2;
    public long playerPermission;
    public long customFlags;
    public long entityID;

    public Packet37AdventureSettings(Set<Flag> setFlags) {
        setFlags.forEach(flag -> setFlag(flag, true));
    }

    @Override
    public int getId() {
        return 0x37;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.flags = buffer.readUnsignedVarLong();
        this.commandPermissions = CommandPermissionLevel.values()[(int) buffer.readUnsignedVarLong()];
        this.flags2 = buffer.readUnsignedVarLong();
        this.playerPermission = buffer.readUnsignedVarLong();
        this.customFlags = buffer.readUnsignedVarLong();
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeUnsignedVarLong(this.flags);
        buffer.writeUnsignedVarLong(this.commandPermissions.ordinal());
        buffer.writeUnsignedVarLong(this.flags2);
        buffer.writeUnsignedVarLong(this.playerPermission);
        buffer.writeUnsignedVarLong(this.customFlags);
        buffer.writeUnsignedLong(this.entityID);
    }

    public Packet37AdventureSettings flag(Flag flag, boolean setOrUnset) {
        setFlag(flag, setOrUnset);
        return this;
    }

    public void setFlag(Flag theFlag, boolean setOrUnset) {
        int flag = theFlag.mask;
        boolean flags = (flag & BITFLAG_SECOND_SET) != 0;
        if(setOrUnset)
            if(flags)
                this.flags2 |= flag;
            else
                this.flags |= flag;
        else if(flags)
            this.flags2 &= ~flag;
        else
            this.flags &= ~flag;
    }

    public enum CommandPermissionLevel {
        NORMAL,
        OPERATOR,
        HOST,
        AUTOMATION,
        ADMIN
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
        private final boolean defaultValue;

        Flag(int mask, boolean defaultValue) {
            this.mask = mask;
            this.defaultValue = defaultValue;
        }

        public boolean hasDefaultValue() {
            return this.defaultValue;
        }

    }

}
