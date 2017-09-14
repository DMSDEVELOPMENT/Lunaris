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

    @Override
    public int getId() {
        return 0x0b;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.putVarLong(this.entityUniqueId);
        buffer.putVarLong(this.entityRuntimeId);
        buffer.putVarInt(this.playerGamemode);
        buffer.writeVector3f(this.x, this.y, this.z);
        buffer.writeLFloat(this.yaw);
        buffer.writeLFloat(this.pitch);
        buffer.putVarInt(this.seed);
        buffer.putVarInt(this.dimension);
        buffer.putVarInt(this.generator);
        buffer.putVarInt(this.gamemode);
        buffer.putVarInt(this.difficulty);
        buffer.writeBlockVector(this.spawnX, this.spawnY, this.spawnZ);
        buffer.writeBoolean(this.hasAchievementsDisabled);
        buffer.putVarInt(this.dayCycleStopTime);
        buffer.writeBoolean(this.eduMode);
        buffer.writeLFloat(this.rainLevel);
        buffer.writeLFloat(this.lightningLevel);
        buffer.writeBoolean(this.multiplayerGame);
        buffer.writeBoolean(this.broadcastToLAN);
        buffer.writeBoolean(this.broadcastToXboxLive);
        buffer.writeBoolean(this.commandsEnabled);
        buffer.writeBoolean(this.isTexturePacksRequired);
        buffer.writeVarIntLonger(this.ruleDatas.length);
        for(RuleData rule : this.ruleDatas) {
            buffer.writeStringUnlimited(rule.name);
            buffer.writeBoolean(rule.unknown1);
            buffer.writeBoolean(rule.unknown2);
        }
        buffer.writeBoolean(this.bonusChest);
        buffer.writeBoolean(this.trustPlayers);
        buffer.putVarInt(this.permissionLevel);
        buffer.putVarInt(this.gamePublish);
        buffer.writeStringUnlimited(this.levelId);
        buffer.writeStringUnlimited(this.worldName);
        buffer.writeStringUnlimited(this.premiumWorldTemplateId);
        buffer.writeBoolean(this.unknown);
        buffer.writeLLong(this.currentTick);
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
