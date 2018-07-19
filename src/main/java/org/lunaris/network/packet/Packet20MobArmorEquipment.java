package org.lunaris.network.packet;

import io.gomint.jraknet.PacketBuffer;
import org.lunaris.api.item.ItemStack;
import org.lunaris.network.Packet;
import org.lunaris.network.util.SerializationUtil;

/**
 * Created by RINES on 01.10.17.
 */
public class Packet20MobArmorEquipment extends Packet {

    private long entityID;
    private ItemStack[] armor = new ItemStack[4];

    public Packet20MobArmorEquipment() {}

    public Packet20MobArmorEquipment(long entityID, ItemStack[] armor) {
        this.entityID = entityID;
        this.armor = armor;
    }

    @Override
    public byte getID() {
        return 0x20;
    }

    @Override
    public void read(PacketBuffer buffer) {
        this.entityID = buffer.readUnsignedVarLong();
        for(int i = 0; i < this.armor.length; ++i)
            this.armor[i] = SerializationUtil.readItemStack(buffer);
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUnsignedVarLong(this.entityID);
        for(ItemStack is : this.armor)
            SerializationUtil.writeItemStack(is, buffer);
    }

}
