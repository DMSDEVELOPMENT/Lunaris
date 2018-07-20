package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.api.item.ItemStack;
import org.lunaris.entity.LPlayer;
import org.lunaris.entity.data.EntityMetadata;
import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;

import java.util.UUID;

/**
 * Created by RINES on 16.09.17.
 */
public class Packet0CAddPlayer extends Packet {

    private UUID uuid;
    private String name;
    private String thirdPartyName = "";  // TODO: Find out if this is some sort of nickname function
    private int platformID;         // TODO: The heck is this? (I guess the servers platform?)
    private long entityId;
    private long runtimeEntityId;

    private String unknown = "";    // TODO: What is this?

    private float x;
    private float y;
    private float z;

    private float velocityX;
    private float velocityY;
    private float velocityZ;

    private float pitch;
    private float headYaw;
    private float yaw;

    private ItemStack itemInHand;
    private EntityMetadata metadata;

    // Some adventure stuff? Yep this is adventure setting stuff
    private int flags;
    private int commandPermission;
    private int flags2;
    private int playerPermission;
    private int customFlags;

    public Packet0CAddPlayer() {
    }

    public Packet0CAddPlayer(LPlayer player) {
        this.uuid = player.getUUID();
        this.name = player.getName();
        this.entityId = player.getEntityID();
        this.runtimeEntityId = player.getEntityID();
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
        this.velocityX = player.getMotionX();
        this.velocityY = player.getMotionY();
        this.velocityZ = player.getMotionZ();
        this.yaw = player.getYaw();
        this.headYaw = player.getHeadYaw();
        this.pitch = player.getPitch();
        this.itemInHand = player.getInventory().getItemInHand();
        this.metadata = player.getDataProperties();
    }

    @Override
    public byte getID() {
        return 0x0c;
    }

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUUID(this.uuid);
        buffer.writeString(this.name);

        buffer.writeString(this.thirdPartyName);
        buffer.writeSignedVarInt(this.platformID);

        buffer.writeSignedVarLong(this.entityId);
        buffer.writeUnsignedVarLong(this.runtimeEntityId);

        buffer.writeString(this.uuid.toString());

        buffer.writeLFloat(this.x);
        buffer.writeLFloat(this.y);
        buffer.writeLFloat(this.z);

        buffer.writeLFloat(this.velocityX);
        buffer.writeLFloat(this.velocityY);
        buffer.writeLFloat(this.velocityZ);

        buffer.writeLFloat(this.pitch);
        buffer.writeLFloat(this.headYaw);
        buffer.writeLFloat(this.yaw);

        SerializationUtil.writeItemStack(this.itemInHand, buffer);
        SerializationUtil.writeMetadata(this.metadata, buffer);

        buffer.writeUnsignedVarInt(this.flags);
        buffer.writeUnsignedVarInt(this.commandPermission);
        buffer.writeUnsignedVarInt(this.flags2);
        buffer.writeUnsignedVarInt(this.playerPermission);
        buffer.writeUnsignedVarInt(this.customFlags);

        buffer.writeLLong(this.entityId);

        // write Entity Links - idk what is this, so there is 0 links
        buffer.writeUnsignedVarInt(0);
    }
}
