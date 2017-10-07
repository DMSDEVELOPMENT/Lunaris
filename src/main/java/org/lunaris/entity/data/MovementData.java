package org.lunaris.entity.data;

import org.lunaris.block.Block;
import org.lunaris.entity.Entity;
import org.lunaris.entity.misc.EntityType;
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
        if (Math.abs(this.x - x) < LMath.EPSILON && Math.abs(this.y - y) < LMath.EPSILON && Math.abs(this.z - z) < LMath.EPSILON)
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
        if (Math.abs(this.yaw - yaw) < LMath.EPSILON && Math.abs(this.headYaw - headYaw) < LMath.EPSILON && Math.abs(this.pitch - pitch) < LMath.EPSILON)
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
        // Check if we need to calc motion
        this.lastUpdateDt += dT;
        if (this.lastUpdateDt >= TICK_RATE) {
            // Calc motion
            changeMotion(0, -GRAVITY, 0);

            // Check if we are stuck in a block
            checkWhetherInsideBlocks(this.entity.getWorld());

            // Move by motion amount
            float movX = this.motionX;
            float movY = this.motionY;
            float movZ = this.motionZ;

            // Security check so we don't move and collect bounding boxes like crazy
            if (Math.abs(movX) > 20 || Math.abs(movZ) > 20 || Math.abs(movY) > 20) {
                return;
            }

            float dX = movX;
            float dY = movY;
            float dZ = movZ;

            AxisAlignedBB oldBoundingBox = this.entity.getBoundingBox().clone();

            // Check if we collide with some blocks when we would move that fast
            List<AxisAlignedBB> collisionList = getCollisionCubes(this.entity, this.entity.getBoundingBox().getOffsetBoundingBox(dX, dY, dZ), false);
            if (collisionList != null) {
                // Check if we would hit a y border block
                for (AxisAlignedBB axisAlignedBB : collisionList) {
                    dY = axisAlignedBB.calculateYOffset(this.entity.getBoundingBox(), dY);
                }

                this.entity.getBoundingBox().offset(0, dY, 0);

                // Check if we would hit a x border block
                for (AxisAlignedBB axisAlignedBB : collisionList) {
                    dX = axisAlignedBB.calculateXOffset(this.entity.getBoundingBox(), dX);
                }

                this.entity.getBoundingBox().offset(dX, 0, 0);

                // Check if we would hit a z border block
                for (AxisAlignedBB axisAlignedBB : collisionList) {
                    dZ = axisAlignedBB.calculateZOffset(this.entity.getBoundingBox(), dZ);
                }

                this.entity.getBoundingBox().offset(0, 0, dZ);
            } else {
                this.entity.getBoundingBox().offset(dX, dY, dZ);
            }

            // Check if we can jump
            boolean notFallingFlag = (this.entity.isOnGround() || (dY != movY && movY < 0));
            if (this.entity.getStepHeight() > 0 && notFallingFlag && this.jumpingOffset < 0.05 && (movX != dX || movZ != dZ)) {
                float oldDX = dX;
                float oldDY = dY;
                float oldDZ = dZ;

                dX = movX;
                dY = this.entity.getStepHeight();
                dZ = movZ;

                // Save and restore old bounding box
                AxisAlignedBB oldBoundingBox1 = this.entity.getBoundingBox().clone();
                this.entity.getBoundingBox().setBounds(oldBoundingBox);

                // Check for collision
                collisionList = getCollisionCubes(this.entity, this.entity.getBoundingBox().addCoordinates(dX, dY, dZ), false);
                if (collisionList != null) {
                    // Check if we would hit a y border block
                    for (AxisAlignedBB axisAlignedBB : collisionList) {
                        dY = axisAlignedBB.calculateYOffset(this.entity.getBoundingBox(), dY);
                    }

                    this.entity.getBoundingBox().offset(0, dY, 0);

                    // Check if we would hit a x border block
                    for (AxisAlignedBB axisAlignedBB : collisionList) {
                        dX = axisAlignedBB.calculateXOffset(this.entity.getBoundingBox(), dX);
                    }

                    this.entity.getBoundingBox().offset(dX, 0, 0);

                    // Check if we would hit a z border block
                    for (AxisAlignedBB axisAlignedBB : collisionList) {
                        dZ = axisAlignedBB.calculateZOffset(this.entity.getBoundingBox(), dZ);
                    }

                    this.entity.getBoundingBox().offset(0, 0, dZ);
                }

                // Check if we moved left or right
                if (LMath.pow2(oldDX) + LMath.pow2(oldDZ) >= LMath.pow2(dX) + LMath.pow2(dZ)) {
                    // Revert this decision of moving the bounding box up
                    dX = oldDX;
                    dY = oldDY;
                    dZ = oldDZ;
                    this.entity.getBoundingBox().setBounds(oldBoundingBox1);
                } else {
                    // Move the bounding box up by .5
                    this.jumpingOffset += 0.5;
                }
            }

            // Move by new bounding box
            if (dX != 0.0 || dY != 0.0 || dZ != 0.0) {
                AxisAlignedBB bb = this.entity.getBoundingBox();
                setPosition(
                        (bb.getMinX() + bb.getMaxX()) / 2,
                        bb.getMinY() + this.jumpingOffset,
                        (bb.getMinZ() + bb.getMaxZ()) / 2
                );
            }

            // Check for grounding states
            this.entity.setupCollisionFlags(movX, movY, movZ, dX, dY, dZ);
            this.entity.setupFallDistance(dY);

            // We did not move so we collided, set motion to 0 to escape hell
            if (movX != dX) {
                this.motionX = 0F;
            }

            if (movY != dY) {
                this.motionY = 0F;
            }

            if (movZ != dZ) {
                this.motionZ = 0F;
            }

            // Reset last update
            this.lastUpdateDt = 0;
        }

        // Check if we need to update the bounding box
        if (isDirty()) {
            float hwidth = this.entity.getWidth() / 2;
            this.entity.getBoundingBox().setBounds(
                    this.x - hwidth,
                    this.y,
                    this.z - hwidth,
                    this.x + hwidth,
                    this.y + this.entity.getHeight(),
                    this.z + hwidth
            );
            this.dirty = true;
        }
    }

    private void checkWhetherInsideBlocks(World world) {
        if(this.entity.getEntityType() == EntityType.PLAYER)
            return;
        AxisAlignedBB bb = this.entity.getBoundingBox();
        int bx = LMath.fastFloor(this.x);
        int by = LMath.fastFloor(this.y);
        int bz = LMath.fastFloor(this.z);
        Block block;
        if ((block = world.getBlockAt(bx, by, bz)).getHandle().isSolid() && block.getBoundingBox().intersectsWith(bb)) {
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
            if (valid && force < this.force) {
                this.direction = (byte) direction;
                this.force = force;
            }
        }

        private void accept(MovementData movement) {
            int sign = (this.direction & 1) == 0 ? -1 : 1;
            float force = sign * ((float) Math.random() * .2F + .1F);
            if ((this.direction & 2) != 0)
                movement.changeMotion(0F, force, 0F);
            else if ((this.direction & 4) != 0)
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

        for (int z = minZ; z < maxZ; ++z)
            for (int x = minX; x < maxX; ++x)
                for (int y = minY; y < maxY; ++y) {
                    Block block = world.getBlockAt(x, y, z);
                    if (!block.getHandle().canPassThrough()) {
                        AxisAlignedBB blockBox = block.getBoundingBox();
                        if (blockBox.intersectsWith(bb)) {
                            if (collisions == null)
                                collisions = new ArrayList<>();
                            collisions.add(blockBox);
                        }
                    }
                }
        if (includeEntities) {
            Collection<Entity> entities = getNearbyEntities(theEntity, bb.grow(.25F, .25F, .25F));
            if (entities != null)
                for (Entity entity : entities) {
                    if (collisions == null)
                        collisions = new ArrayList<>();
                    collisions.add(entity.getBoundingBox());
                }
        }
        return collisions;
    }

    private static Collection<Entity> getNearbyEntities(Entity theEntity, AxisAlignedBB bb) {
        World world = theEntity.getWorld();
        Set<Entity> result = null;
        for (Entity entity : world.getEntities())
            if (entity.getBoundingBox().intersectsWith(bb)) {
                if (result == null)
                    result = new HashSet<>();
                result.add(entity);
            }
        if (result != null)
            result.remove(theEntity);
        return result;
    }

    private float normalize(float value) {
        while (value < -180F)
            value += 360F;
        while (value > 180F)
            value -= 360F;
        return value;
    }

}
