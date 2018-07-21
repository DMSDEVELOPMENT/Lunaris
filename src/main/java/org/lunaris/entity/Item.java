package org.lunaris.entity;

import org.lunaris.api.entity.EntityType;
import org.lunaris.api.item.ItemStack;
import org.lunaris.entity.data.EntityDataFlag;
import org.lunaris.network.Packet;
import org.lunaris.network.packet.Packet0FAddItem;
import org.lunaris.util.math.LMath;

/**
 * Created by RINES on 04.10.17.
 */
public class Item extends LEntity {

    private final ItemStack itemStack;
    private long pickupDelay;
    private float lastUpdateDt;

    Item(long entityID, ItemStack itemStack) {
        this(entityID, itemStack, 500L);
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
    public void tick(long current, float dT) {
        super.tick(current, dT);
        this.lastUpdateDt += dT;
        if (this.lastUpdateDt >= .05F) {
            final float defaultFriction = 1F - .02F; //.02F = DRAG constant
            float friction = defaultFriction;
            if (isOnGround() && (Math.abs(getMotionX()) > LMath.EPSILON || Math.abs(getMotionZ()) > LMath.EPSILON))
                friction *= getLocation().add(0D, -1D, 0D).getBlock().getHandle().getFrictionFactor();
            setMotion(getMotionX() * friction, getMotionY() * defaultFriction * (isOnGround() ? -.5F : 1F), getMotionZ() * friction);
            this.lastUpdateDt = 0F;
        }
    }

    @Override
    public void fall() {

    }

    @Override
    public Packet createSpawnPacket() {
        return new Packet0FAddItem(this);
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public long getPickupDelay() {
        return this.pickupDelay;
    }

}
