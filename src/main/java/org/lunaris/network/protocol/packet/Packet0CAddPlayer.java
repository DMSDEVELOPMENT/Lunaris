package org.lunaris.network.protocol.packet;

import org.lunaris.entity.LPlayer;
import org.lunaris.entity.data.EntityMetadata;
import org.lunaris.item.ItemStack;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

import java.util.UUID;

/**
 * Created by RINES on 16.09.17.
 */
public class Packet0CAddPlayer extends MinePacket {

    private UUID uuid;
    private String name;
    private long entityId;
    private float x, y, z, speedX, speedY, speedZ;
    private float yaw, headYaw, pitch;
    private ItemStack hand;
    private EntityMetadata metadata;

    public Packet0CAddPlayer() {}

    public Packet0CAddPlayer(LPlayer player) {
        this.uuid = player.getUUID();
        this.name = player.getName();
        this.entityId = player.getEntityID();
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
        this.speedX = player.getMotionX();
        this.speedY = player.getMotionY();
        this.speedZ = player.getMotionZ();
        this.yaw = player.getYaw();
        this.headYaw = player.getHeadYaw();
        this.pitch = player.getPitch();
        this.hand = player.getInventory().getItemInHand();
        this.metadata = player.getDataProperties();
    }

    @Override
    public int getId() {
        return 0x0c;
    }

    @Override
    public void read(MineBuffer buffer) {

    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeUUID(this.uuid);
        buffer.writeString(this.name);
        buffer.writeEntityUniqueId(this.entityId);
        buffer.writeEntityRuntimeId(this.entityId);
        buffer.writeVector3f(this.x, this.y, this.z);
        buffer.writeVector3f(this.speedX, this.speedY, this.speedZ);
        buffer.writeFloat(this.pitch);
        buffer.writeFloat(this.headYaw);
        buffer.writeFloat(this.yaw);
        buffer.writeItemStack(this.hand);
        buffer.writeMetadata(this.metadata);

    }
}
