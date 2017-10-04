package org.lunaris.entity;

import org.lunaris.entity.misc.EntityType;
import org.lunaris.item.ItemStack;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.network.protocol.packet.Packet0FAddItem;

/**
 * Created by RINES on 04.10.17.
 */
public class Item extends Entity {

    private final ItemStack itemStack;
    private long pickupDelay;

    Item(long entityID, ItemStack itemStack) {
        this(entityID, itemStack, 2000L);
    }

    Item(long entityID, ItemStack itemStack, long pickupDelay) {
        super(entityID, EntityType.ITEM_DROP);
        this.itemStack = itemStack;
        setPickupDelay(pickupDelay);
    }

    public void setPickupDelay(long millis) {
        this.pickupDelay = System.currentTimeMillis() + millis;
    }

    @Override
    public float getHeight() {
        return .4F;
    }

    @Override
    public float getWidth() {
        return .25F;
    }

    @Override
    public float getStepHeight() {
        return .25F / 2F + .1F;
    }

    @Override
    public void fall() {

    }

    @Override
    public MinePacket createSpawnPacket() {
        return new Packet0FAddItem(this);
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public long getPickupDelay() {
        return this.pickupDelay;
    }

}
