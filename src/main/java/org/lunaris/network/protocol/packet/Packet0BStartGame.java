package org.lunaris.network.protocol.packet;

import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 14.09.17.
 */
public class Packet0BStartGame extends MinePacket {

    public long entityUniqueId;
    public long entityRuntimeId;
    public int playerGamemode;
    public float x;
    public float y;
    public float z;
    public float yaw;
    public float pitch;
    public int seed;
    public byte dimension;
    public int generator = 1;
    public int gamemode;
    public int difficulty;
    public int spawnX;
    public int spawnY;
    public int spawnZ;
    public boolean hasAchievementsDisabled = true;
    public int dayCycleStopTime = -1; //-1 = not stopped, any positive value = stopped at that time
    public boolean eduMode = false;
    public float rainLevel;
    public float lightningLevel;
    public boolean multiplayerGame = true;
    public boolean broadcastToLAN = true;
    public boolean broadcastToXboxLive = true;
    public boolean commandsEnabled;
    public boolean isTexturePacksRequired = false;
    public RuleData[] ruleDatas = new RuleData[0];
    public boolean bonusChest = false;
    public boolean trustPlayers = false;
    public int permissionLevel = 1;
    public int gamePublish = 4;
    public String levelId = ""; //base64 string, usually the same as world folder name in vanilla
    public String worldName;
    public String premiumWorldTemplateId = "";
    public boolean unknown = false;
    public long currentTick;
    public int enchantmentSeed;

    @Override
    public int getId() {
        return 0x0b;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeVarLong(this.entityUniqueId);
        buffer.writeEntityRuntimeId(this.entityRuntimeId);
        buffer.writeVarInt(this.playerGamemode);
        buffer.writeVector3f(this.x, this.y, this.z);
        buffer.writeVector2f(this.yaw, this.pitch);
        //level settings begin
        buffer.writeVarInt(this.seed);
        buffer.writeVarInt(this.dimension);
        buffer.writeVarInt(this.generator);
        buffer.writeVarInt(this.gamemode);
        buffer.writeVarInt(this.difficulty);
        buffer.writeBlockVector(this.spawnX, this.spawnY, this.spawnZ);
        buffer.writeBoolean(this.hasAchievementsDisabled);
        buffer.writeVarInt(this.dayCycleStopTime);
        buffer.writeBoolean(this.eduMode);
        buffer.writeFloat(this.rainLevel);
        buffer.writeFloat(this.lightningLevel);
        buffer.writeBoolean(this.multiplayerGame);
        buffer.writeBoolean(this.broadcastToLAN);
        buffer.writeBoolean(this.broadcastToXboxLive);
        buffer.writeBoolean(this.commandsEnabled);
        buffer.writeBoolean(this.isTexturePacksRequired);
        buffer.writeUnsignedVarInt(this.ruleDatas.length);
        for(RuleData rule : this.ruleDatas) {
            buffer.writeString(rule.name);
            buffer.writeBoolean(rule.unknown1);
            buffer.writeBoolean(rule.unknown2);
        }
        buffer.writeBoolean(this.bonusChest);
        buffer.writeBoolean(this.trustPlayers);
        buffer.writeVarInt(this.permissionLevel);
        buffer.writeVarInt(this.gamePublish);
        //level settings end
        buffer.writeString(this.levelId);
        buffer.writeString(this.worldName);
        buffer.writeString(this.premiumWorldTemplateId);
        buffer.writeBoolean(this.unknown);
        buffer.writeUnsignedLong(this.currentTick);
        buffer.writeVarInt(this.enchantmentSeed);
    }

    public class RuleData {

        private final String name;
        private final boolean unknown1;
        private final boolean unknown2;

        public RuleData(String name, boolean unknown1, boolean unknown2) {
            this.name = name;
            this.unknown1 = unknown1;
            this.unknown2 = unknown2;
        }
        
    }

}
