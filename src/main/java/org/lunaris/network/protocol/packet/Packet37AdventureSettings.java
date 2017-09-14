package org.lunaris.network.protocol.packet;

import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet37AdventureSettings extends MinePacket {

    public static final int ACTION_FLAG_PROHIBIT_ALL = 0;
    public static final int ACTION_FLAG_BUILD_AND_MINE = 1;
    public static final int ACTION_FLAG_DOORS_AND_SWITCHES = 2;
    public static final int ACTION_FLAG_OPEN_CONTAINERS = 4;
    public static final int ACTION_FLAG_ATTACK_PLAYERS = 8;
    public static final int ACTION_FLAG_ATTACK_MOBS = 16;
    public static final int ACTION_FLAG_OP = 32;
    public static final int ACTION_FLAG_TELEPORT = 64;
    public static final int ACTION_FLAG_DEFAULT_LEVEL_PERMISSIONS = 128;
    public static final int ACTION_FLAG_ALLOW_ALL = 511;

    public static final int PERMISSION_LEVEL_VISITOR = 0;
    public static final int PERMISSION_LEVEL_MEMBER = 1;
    public static final int PERMISSION_LEVEL_OPERATOR = 2;
    public static final int PERMISSION_LEVEL_CUSTOM = 3;

    public boolean worldImmutable;
    public boolean noPvp;
    public boolean noPvm;
    public boolean noMvp;

    public boolean autoJump;
    public boolean allowFlight;
    public boolean noClip;
    public boolean worldBuilder;
    public boolean isFlying;
    public boolean muted;

    public int flags = 0;
    public int userPermission;
    public int actionPermissions = ACTION_FLAG_DEFAULT_LEVEL_PERMISSIONS;
    public int permissionLevel = PERMISSION_LEVEL_MEMBER;
    public long userId = 0;

    @Override
    public int getId() {
        return 0x37;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.flags = (int) buffer.readVarLong();
        this.userPermission = (int) buffer.readVarLong();
        this.worldImmutable = (this.flags & 1) != 0;
        this.noPvp = (this.flags & (1 << 1)) != 0;
        this.noPvm = (this.flags & (1 << 2)) != 0;
        this.noMvp = (this.flags & (1 << 3)) != 0;

        this.autoJump = (this.flags & (1 << 5)) != 0;
        this.allowFlight = (this.flags & (1 << 6)) != 0;
        this.noClip = (this.flags & (1 << 7)) != 0;
        this.worldBuilder = (this.flags & (1 << 8)) != 0;
        this.isFlying = (this.flags & (1 << 9)) != 0;
        this.muted = (this.flags & (1 << 10)) != 0;
    }

    @Override
    public void write(MineBuffer buffer) {
        if (this.worldImmutable) this.flags |= 1;
        if (this.noPvp) this.flags |= 1 << 1;
        if (this.noPvm) this.flags |= 1 << 2;
        if (this.noMvp) this.flags |= 1 << 3;

        if (this.autoJump) this.flags |= 1 << 5;
        if (this.allowFlight) this.flags |= 1 << 6;
        if (this.noClip) this.flags |= 1 << 7;
        if (this.worldBuilder) this.flags |= 1 << 8;
        if (this.isFlying) this.flags |= 1 << 9;
        if (this.muted) this.flags |= 1 << 10;
        buffer.writeVarLong(this.flags);
        buffer.writeVarLong(this.userPermission);
        buffer.writeVarLong(this.actionPermissions);
        buffer.writeVarLong(this.permissionLevel);
        if ((this.userId & 0x01) != 0) {
            buffer.writeLLong(-1 * ((this.userId + 1) >> 1));
        } else {
            buffer.writeLLong(this.userId >> 1);
        }
    }
}
