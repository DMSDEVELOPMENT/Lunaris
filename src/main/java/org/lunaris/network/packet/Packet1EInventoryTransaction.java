package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;

import org.lunaris.inventory.transaction.InventoryActionData;
import org.lunaris.inventory.transaction.ReleaseItemData;
import org.lunaris.inventory.transaction.TransactionData;
import org.lunaris.inventory.transaction.UseItemData;
import org.lunaris.inventory.transaction.UseItemOnEntityData;
import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;

/**
 * Created by RINES on 01.10.17.
 */
public class Packet1EInventoryTransaction extends Packet {

    private TransactionType type;
    private InventoryActionData[] actions;
    private TransactionData data;

    @Override
    public byte getID() {
        return 0x1e;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.type = TransactionType.values()[buffer.readUnsignedVarInt()];
        this.actions = new InventoryActionData[buffer.readUnsignedVarInt()];
        for (int i = 0; i < this.actions.length; ++i)
            this.actions[i] = new InventoryActionData(buffer);

        switch (this.type) {
            case NORMAL:
            case MISMATCH:
                //Хз че это значит
                //Regular ComplexInventoryTransaction doesn't read any extra data
                break;
            case USE_ITEM: {
                this.data = new UseItemData(
                    UseItemActionType.values()[buffer.readUnsignedVarInt()],
                    SerializationUtil.readBlockVector(buffer),
                    SerializationUtil.readBlockFace(buffer),
                    buffer.readSignedVarInt(),
                    SerializationUtil.readItemStack(buffer),
                    SerializationUtil.readVector3f(buffer),
                    SerializationUtil.readVector3f(buffer)
                );
                break;
            }
            case USE_ITEM_ON_ENTITY: {
                long entityID = buffer.readUnsignedVarLong();
                this.data = new UseItemOnEntityData(
                    UseItemOnEntityActionType.values()[buffer.readUnsignedVarInt()],
                    entityID,
                    buffer.readSignedVarInt(),
                    SerializationUtil.readItemStack(buffer),
                    SerializationUtil.readVector3f(buffer),
                    SerializationUtil.readVector3f(buffer)
                );
                break;
            }
            case RELEASE_ITEM: {
                this.data = new ReleaseItemData(
                    ReleaseItemActionType.values()[buffer.readUnsignedVarInt()],
                    buffer.readSignedVarInt(),
                    SerializationUtil.readItemStack(buffer),
                    SerializationUtil.readVector3f(buffer)
                );
                break;
            }
        }
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUnsignedVarInt(this.type.ordinal());
        buffer.writeUnsignedVarInt(this.actions.length);
        for (InventoryActionData data : this.actions)
            data.write(buffer);
        switch (this.type) {
            case NORMAL:
            case MISMATCH:
                break;
            case USE_ITEM: {
                UseItemData data = (UseItemData) this.data;
                buffer.writeUnsignedVarInt(data.getType().ordinal());
                SerializationUtil.writeBlockVector(data.getBlockPosition(), buffer);
                buffer.writeSignedVarInt(data.getBlockFace().getIndex());
                buffer.writeSignedVarInt(data.getHotbarSlot());
                SerializationUtil.writeItemStack(data.getItemInHand(), buffer);
                SerializationUtil.writeVector3f(data.getPlayerPosition(), buffer);
                SerializationUtil.writeVector3f(data.getClickPosition(), buffer);
                break;
            }
            case USE_ITEM_ON_ENTITY: {
                UseItemOnEntityData data = (UseItemOnEntityData) this.data;
                buffer.writeUnsignedVarLong(data.getEntityID());
                buffer.writeUnsignedVarInt(data.getType().ordinal());
                buffer.writeSignedVarInt(data.getHotbarSlot());
                SerializationUtil.writeItemStack(data.getItemInHand(), buffer);
                SerializationUtil.writeVector3f(data.getVector1(), buffer);
                SerializationUtil.writeVector3f(data.getVector2(), buffer);
                break;
            }
            case RELEASE_ITEM: {
                ReleaseItemData data = (ReleaseItemData) this.data;
                buffer.writeUnsignedVarInt(data.getType().ordinal());
                buffer.writeSignedVarInt(data.getHotbarSlot());
                SerializationUtil.writeItemStack(data.getItemInHand(), buffer);
                SerializationUtil.writeVector3f(data.getPlayerPosition(), buffer);
                break;
            }
        }
    }

    public TransactionType getType() {
        return this.type;
    }

    public InventoryActionData[] getActions() {
        return this.actions;
    }

    public TransactionData getData() {
        return this.data;
    }

    public enum TransactionType {
        NORMAL,
        MISMATCH,
        USE_ITEM,
        USE_ITEM_ON_ENTITY,
        RELEASE_ITEM
    }

    public enum UseItemActionType {
        CLICK_BLOCK,
        CLICK_AIR,
        BREAK_BLOCK
    }

    public enum UseItemOnEntityActionType {
        INTERACT,
        ATTACK
    }

    public enum ReleaseItemActionType {
        RELEASE, //bow shoot
        CONSUME //eat food, drink potions, etc
    }

    public enum MagicActionType {
        DROP_ITEM,
        PICKUP_ITEM
    }

    public enum CreativeMagicActionType {
        DELETE_ITEM,
        CREATE_ITEM
    }

}
