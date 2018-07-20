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
    public long entityId;
    public long runtimeEntityId;
    public int gamemode;
    public Location spawn;

    // Level data
    public int seed;
    public int dimension;
    public int generator = 1;
    public int worldGamemode;
    public int difficulty;
    public int worldSpawnX;
    public int worldSpawnY;
    public int worldSpawnZ;
    public boolean hasAchievementsDisabled = true;
    public int dayCycleStopTime = -1; //-1 = not stopped, any positive value = stopped at that time
    public boolean eduMode;
    public float rainLevel;
    public float lightningLevel;
    public boolean isMultiplayerGame = true;
    public boolean hasLANBroadcast = true;
    public boolean hasXboxLiveBroadcast = false;
    public boolean commandsEnabled;
    public boolean isTexturePacksRequired;

    // Gamerule data
    public Map<Gamerule, Object> gamerules;
    public boolean hasBonusChestEnabled;
    public boolean hasStartWithMapEnabled;
    public boolean hasTrustPlayersEnabled;
    public int defaultPlayerPermission = 1;
    public int xboxLiveBroadcastMode = 0;
    public boolean hasPlatformBroadcast = false;
    public int platformBroadcastMode = 0;
    public boolean xboxLiveBroadcastIntent = false;

    // World data
    public String levelId; //base64 string, usually the same as world folder name in vanilla
    public String worldName;
    public String templateName = "";
    public boolean unknown1 = true;
    public long currentTick;
    public int enchantmentSeed;

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
