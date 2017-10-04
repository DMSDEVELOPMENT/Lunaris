package org.lunaris.entity.data;

import org.lunaris.block.Block;
import org.lunaris.entity.Entity;
import org.lunaris.entity.misc.Movable;
import org.lunaris.util.math.AxisAlignedBB;
import org.lunaris.util.math.LMath;
import org.lunaris.world.Location;
import org.lunaris.world.World;

import java.util.*;

/**
 * Created by RINES on 30.09.17.
 */
public class MovementData implements Movable {

    private final static float TICK_RATE = .05F;
    private final static float GRAVITY = 0.04F;

    private final Entity entity;

    private float x, y, z;
    private float motionX, motionY, motionZ;
    private float yaw, headYaw, pitch;

    private boolean dirty;
    private float lastUpdateDt;

    private float jumpingOffset;

    public MovementData(Entity entity) {
        this.entity = entity;
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public float getZ() {
        return this.z;
    }

    @Override
    public float getMotionX() {
        return this.motionX;
    }

    @Override
    public float getMotionY() {
        return this.motionY;
    }

    @Override
    public float getMotionZ() {
        return this.motionZ;
    }

    @Override
    public float getYaw() {
        return this.yaw;
    }

    @Override
    public float getHeadYaw() {
        return this.headYaw;
    }

    @Override
    public float getPitch() {
        return this.pitch;
    }

    @Override
    public Location getLocation(World world) {
        return new Location(world, this.x, this.y, this.z, this.yaw, this.headYaw, this.pitch);
    }

    @Override
    public void setPosition(float x, float y, float z) {
        if(Math.abs(this.x - x) < LMath.EPSILON && Math.abs(this.y - y) < LMath.EPSILON && Math.abs(this.z - z) < LMath.EPSILON)
            return;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dirty = true;
    }

    @Override
    public void setRotation(float yaw, float headYaw, float pitch) {
        yaw = normalize(yaw);
        headYaw = normalize(headYaw);
        pitch = normalize(pitch);
        if(Math.abs(this.yaw - yaw) < LMath.EPSILON && Math.abs(this.headYaw - headYaw) < LMath.EPSILON && Math.abs(this.pitch - pitch) < LMath.EPSILON)
            return;
        this.yaw = yaw;
        this.headYaw = headYaw;
        this.pitch = pitch;
        this.dirty = true;
    }

    @Override
    public void setMotion(float x, float y, float z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    public boolean isDirty() {
        boolean result = this.dirty;
        this.dirty = false;
        return result;
    }

    public void tickMovement(long current, float dT) {
        if((this.lastUpdateDt += dT) >= TICK_RATE) {
            changeMotion(0, -GRAVITY, 0);
            checkWhetherInsideBlocks(this.entity.getWorld());
            float mx = this.motionX, my = this.motionY, mz = this.motionZ;
            if(Math.abs(mx) > 20 || Math.abs(my) > 20 || Math.abs(mz) > 20)
                return;
            float dx = mx, dy = my, dz = mz;
            AxisAlignedBB oldBox = this.entity.getBoundingBox().clone();
            List<AxisAlignedBB> collisionList = getCollisionCubes(this.entity, this.entity.getBoundingBox().getOffsetBoundingBox(dx, dy, dz), false);
            if(collisionList != null && !collisionList.isEmpty()) {
                for(AxisAlignedBB bb : collisionList)
                    dy = bb.calculateYOffset(this.entity.getBoundingBox(), dy);
                this.entity.getBoundingBox().offset(0F, dy, 0F);
                for(AxisAlignedBB bb : collisionList)
                    dx = bb.calculateXOffset(this.entity.getBoundingBox(), dx);
                this.entity.getBoundingBox().offset(dx, 0F, 0F);
                for(AxisAlignedBB bb : collisionList)
                    dz = bb.calculateZOffset(this.entity.getBoundingBox(), dz);
                this.entity.getBoundingBox().offset(0F, 0F, dz);
            }else
                this.entity.getBoundingBox().offset(dx, dy, dz);
            boolean notFalling = this.entity.isOnGround() || dy != my && my < 0;
            //Check if we can jump
            if(this.entity.getStepHeight() > 0 && notFalling && this.jumpingOffset < .05F && (mx != dx || mz != dz)) {
                float oldDx = dx, oldDy = dy, oldDz = dz;
                dx = mx;
                dy = this.entity.getStepHeight();
                dz = mz;
                AxisAlignedBB box = this.entity.getBoundingBox().clone();
                this.entity.getBoundingBox().setBounds(oldBox);
                collisionList = getCollisionCubes(this.entity, this.entity.getBoundingBox().addCoordinates(dx, dy, dz), false);
                if(collisionList != null && !collisionList.isEmpty()) {
                    for(AxisAlignedBB bb : collisionList)
                        dy = bb.calculateYOffset(this.entity.getBoundingBox(), dy);
                    this.entity.getBoundingBox().offset(0F, dy, 0F);
                    for(AxisAlignedBB bb : collisionList)
                        dx = bb.calculateXOffset(this.entity.getBoundingBox(), dx);
                    this.entity.getBoundingBox().offset(dx, 0F, 0F);
                    for(AxisAlignedBB bb : collisionList)
                        dz = bb.calculateZOffset(this.entity.getBoundingBox(), dz);
                    this.entity.getBoundingBox().offset(0F, 0F, dz);
                }
                if(LMath.pow2(oldDx) + LMath.pow2(oldDz) >= LMath.pow2(dx) + LMath.pow2(dz)) {
                    dx = oldDx;
                    dy = oldDy;
                    dz = oldDz;
                    this.entity.getBoundingBox().setBounds(box);
                    this.jumpingOffset = 0F;
                }else
                    this.jumpingOffset += .5F;
            }
            if(dx != 0F || dy != 0F || dz != 0F) {
                AxisAlignedBB bb = this.entity.getBoundingBox();
                setPosition((bb.getMinX() + bb.getMaxX()) / 2, bb.getMinY() + this.jumpingOffset, (bb.getMinZ() + bb.getMaxZ()) / 2);
            }
            this.entity.setupCollisionFlags(mx, my, mz, dx, dy, dz);
            this.entity.setupFallDistance(dy);
            if(mx != dx)
                this.motionX = 0F;
            if(my != dy)
                this.motionY = 0F;
            if(mz != dz)
                this.motionZ = 0F;
            this.lastUpdateDt = 0F;
        }
        if(isDirty()) {
            float hwidth = this.entity.getWidth() / 2;
            this.entity.getBoundingBox().setBounds(
                    getX() - hwidth,
                    getY(),
                    getZ() - hwidth,
                    getX() + hwidth,
                    getY() + this.entity.getHeight(),
                    getZ() + hwidth
            );
            this.dirty = true;
        }
    }

    private void checkWhetherInsideBlocks(World world) {
        AxisAlignedBB bb = this.entity.getBoundingBox();
        int bx = LMath.fastFloor(this.x);
        int by = LMath.fastFloor(this.y);
        int bz = LMath.fastFloor(this.z);
        Block block;
        if((block = world.getBlockAt(bx, by, bz)).getHandle().isSolid() && block.getBoundingBox().intersectsWith(bb)) {
            float diffX = this.x - bx, diffY = this.y - by, diffZ = this.z - bz;
            boolean freeMinusX = !world.getBlockAt(bx - 1, by, bz).getHandle().isSolid();
            boolean freeMinusY = !world.getBlockAt(bx, by - 1, bz).getHandle().isSolid();
            boolean freeMinusZ = !world.getBlockAt(bx, by, bz - 1).getHandle().isSolid();
            boolean freePlusX = !world.getBlockAt(bx + 1, by, bz).getHandle().isSolid();
            boolean freePlusY = !world.getBlockAt(bx, by + 1, bz).getHandle().isSolid();
            boolean freePlusZ = !world.getBlockAt(bx, by, bz + 1).getHandle().isSolid();
            DirectionPushData push = new DirectionPushData();
            push.check(freeMinusX, 0, diffX);
            push.check(freePlusX, 1, 1 - diffX);
            push.check(freeMinusY, 2, diffY);
            push.check(freePlusY, 3, 1 - diffY);
            push.check(freeMinusZ, 4, diffZ);
            push.check(freePlusZ, 5, 1 - diffZ);
            push.accept(this);
        }
    }

    private static class DirectionPushData {

        private byte direction = -1;
        private float force = 9999F;

        private void check(boolean valid, int direction, float force) {
            if(valid && force < this.force) {
                this.direction = (byte) direction;
                this.force = force;
            }
        }

        private void accept(MovementData movement) {
            int sign = (this.direction & 1) == 0 ? -1 : 1;
            float force = sign * ((float) Math.random() * .2F + .1F);
            if((this.direction & 2) != 0)
                movement.changeMotion(0F, force, 0F);
            else if((this.direction & 4) != 0)
                movement.changeMotion(0F, 0F, force);
            else
                movement.changeMotion(force, 0F, 0F);
        }

    }

    private static List<AxisAlignedBB> getCollisionCubes(Entity theEntity, AxisAlignedBB bb, boolean includeEntities) {
        World world = theEntity.getWorld();
        int minX = LMath.fastFloor(bb.getMinX());
        int minY = LMath.fastFloor(bb.getMinY());
        int minZ = LMath.fastFloor(bb.getMinZ());
        int maxX = LMath.fastCeil(bb.getMaxX());
        int maxY = LMath.fastCeil(bb.getMaxY());
        int maxZ = LMath.fastCeil(bb.getMaxZ());

        List<AxisAlignedBB> collisions = null;

        for(int z = minZ; z < maxZ; ++z)
            for(int x = minX; x < maxX; ++x)
                for(int y = minY; y < maxY; ++y) {
                    Block block = world.getBlockAt(x, y, z);
                    if(!block.getHandle().canPassThrough()) {
                        AxisAlignedBB blockBox = block.getBoundingBox();
                        if(blockBox.intersectsWith(bb)) {
                            if(collisions == null)
                                collisions = new ArrayList<>();
                            collisions.add(blockBox);
                        }
                    }
                }
        if(includeEntities) {
            Collection<Entity> entities = getNearbyEntities(theEntity, bb.grow(.25F, .25F, .25F));
            if(entities != null)
                for(Entity entity : entities) {
                    if(collisions == null)
                        collisions = new ArrayList<>();
                    collisions.add(entity.getBoundingBox());
                }
        }
        return collisions;
    }

    private static Collection<Entity> getNearbyEntities(Entity theEntity, AxisAlignedBB bb) {
        World world = theEntity.getWorld();
        Set<Entity> result = null;
        for(Entity entity : world.getEntities())
            if(entity.getBoundingBox().intersectsWith(bb)) {
                if(result == null)
                    result = new HashSet<>();
                result.add(entity);
            }
        if(result != null)
            result.remove(theEntity);
        return result;
    }

    private float normalize(float value) {
        while(value < -180F)
            value += 360F;
        while(value > 180F)
            value -= 360F;
        return value;
    }

}
