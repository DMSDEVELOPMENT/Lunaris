package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;

import org.lunaris.api.world.Gamerule;
import org.lunaris.api.world.Location;
import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;

import java.util.Map;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet0BStartGame extends Packet {

    // Entity data
    private long entityId;
    private long runtimeEntityId;
    private int gamemode;
    private Location spawn;

    // Level data
    private int seed;
    private int dimension;
    private int generator = 1;
    private int worldGamemode;
    private int difficulty;
    private int x;
    private int y;
    private int z;
    private boolean hasAchievementsDisabled = true;
    private int dayCycleStopTime = -1; //-1 = not stopped, any positive value = stopped at that time
    private boolean eduMode;
    private float rainLevel;
    private float lightningLevel;
    private boolean isMultiplayerGame = true;
    private boolean hasLANBroadcast = true;
    private boolean hasXboxLiveBroadcast = false;
    private boolean commandsEnabled;
    private boolean isTexturePacksRequired;

    // Gamerule data
    private Map<Gamerule, Object> gamerules;
    private boolean hasBonusChestEnabled;
    private boolean hasStartWithMapEnabled;
    private boolean hasTrustPlayersEnabled;
    private int defaultPlayerPermission = 1;
    private int xboxLiveBroadcastMode = 0;
    private boolean hasPlatformBroadcast = false;
    private int platformBroadcastMode = 0;
    private boolean xboxLiveBroadcastIntent = false;

    // World data
    private String levelId; //base64 string, usually the same as world folder name in vanilla
    private String worldName;
    private String templateName;
    private boolean unknown1 = true;
    private long currentTick;
    private int enchantmentSeed;

    @Override
    public byte getID() {
        return 0x0b;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeSignedVarLong(this.entityId); // EntityUnique
        buffer.writeUnsignedVarLong(this.runtimeEntityId); // EntityRuntime
        buffer.writeSignedVarInt(this.gamemode); // VarInt
        buffer.writeLFloat((float) this.spawn.getX()); // Vec3
        buffer.writeLFloat((float) this.spawn.getY());
        buffer.writeLFloat((float) this.spawn.getZ());
        buffer.writeLFloat((float) this.spawn.getYaw()); // Vec2
        buffer.writeLFloat((float) this.spawn.getPitch());

        // LevelSettings
        buffer.writeSignedVarInt(this.seed);
        buffer.writeSignedVarInt(this.dimension);
        buffer.writeSignedVarInt(this.generator);
        buffer.writeSignedVarInt(this.worldGamemode);
        buffer.writeSignedVarInt(this.difficulty);
        buffer.writeSignedVarInt((int) this.spawn.getX());
        buffer.writeUnsignedVarInt((int) this.spawn.getY());
        buffer.writeSignedVarInt((int) this.spawn.getZ());
        buffer.writeBoolean(this.hasAchievementsDisabled);
        buffer.writeSignedVarInt(this.dayCycleStopTime);
        buffer.writeBoolean(this.eduMode);
        buffer.writeBoolean(true); // This is hasEduModeEnabled, we default to false until we have all EDU stuff in
        buffer.writeLFloat(this.rainLevel);
        buffer.writeLFloat(this.lightningLevel);
        buffer.writeBoolean(this.isMultiplayerGame);
        buffer.writeBoolean(this.hasLANBroadcast);
        buffer.writeBoolean(this.hasXboxLiveBroadcast);
        buffer.writeBoolean(this.commandsEnabled);
        buffer.writeBoolean(this.isTexturePacksRequired);
        SerializationUtil.writeGamerules(this.gamerules, buffer);
        buffer.writeBoolean(this.hasBonusChestEnabled);
        buffer.writeBoolean(this.hasStartWithMapEnabled);
        buffer.writeBoolean(this.hasTrustPlayersEnabled);
        buffer.writeSignedVarInt(this.defaultPlayerPermission);
        buffer.writeSignedVarInt(this.xboxLiveBroadcastMode);
        buffer.writeInt(32);
        buffer.writeBoolean(this.hasPlatformBroadcast);
        buffer.writeSignedVarInt(this.platformBroadcastMode);
        buffer.writeBoolean(this.xboxLiveBroadcastIntent);
        buffer.writeBoolean(false);
        buffer.writeBoolean(false);
        buffer.writeBoolean(false);

        buffer.writeString(this.levelId);
        buffer.writeString(this.worldName);
        buffer.writeString(this.templateName);
        buffer.writeBoolean(this.unknown1);
        buffer.writeLLong(this.currentTick);
        buffer.writeSignedVarInt(this.enchantmentSeed);
    }
}
