package org.lunaris.network.protocol.packet;

import org.lunaris.item.ItemStack;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;

/**
 * Created by RINES on 01.10.17.
 */
public class Packet20MobArmorEquipment extends MinePacket {

    private long entityID;
    private ItemStack[] armor = new ItemStack[4];

    public Packet20MobArmorEquipment() {}

    public Packet20MobArmorEquipment(long entityID, ItemStack[] armor) {
        this.entityID = entityID;
        this.armor = armor;
    }

    @Override
    public int getId() {
        return 0x20;
    }

    @Override
    public void read(MineBuffer buffer) {
        this.entityID = buffer.readEntityRuntimeId();
        for(int i = 0; i < this.armor.length; ++i)
            this.armor[i] = buffer.readItemStack();
    }

    @Override
    public void write(MineBuffer buffer) {
        buffer.writeEntityRuntimeId(this.entityID);
        for(ItemStack is : this.armor)
            buffer.writeItemStack(is);
    }

}
