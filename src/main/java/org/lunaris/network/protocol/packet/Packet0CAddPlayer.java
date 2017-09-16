package org.lunaris.network.protocol.packet;

import org.lunaris.entity.Player;
import org.lunaris.entity.data.EntityMetadata;
import org.lunaris.item.ItemStack;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.world.Location;

import java.util.UUID;

/**
 * Created by RINES on 16.09.17.
 */
public class Packet0CAddPlayer extends MinePacket {

    private UUID uuid;
    private String name;
    private long entityId;
    private float x, y, z, speedX, speedY, speedZ;
    private float yaw, pitch;
    private ItemStack hand;
    private EntityMetadata metadata;

    public Packet0CAddPlayer() {}

    public Packet0CAddPlayer(Player player) {
        this.uuid = player.getClientUUID();
        this.name = player.getName();
        this.entityId = player.getEntityID();
        Location loc = player.getLocation();
        this.x = (float) loc.getX();
        this.y = (float) loc.getY();
        this.z = (float) loc.getZ();
        this.speedX = this.speedY = this.speedZ = 0F;
        this.yaw = (float) loc.getYaw();
        this.pitch = (float) loc.getPitch();
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
        buffer.writeEntityRuntimeId(this.entityId);
        buffer.writeVector3f(this.x, this.y, this.z);
        buffer.writeVector3f(this.speedX, this.speedY, this.speedZ);
        buffer.writeFloat(this.pitch);
        buffer.writeFloat(this.yaw); //head rotation
        buffer.writeFloat(this.yaw);
        buffer.writeItem(this.hand);
        buffer.writeMetadata(this.metadata);
    }
}
