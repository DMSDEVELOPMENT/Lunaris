package org.lunaris.entity.data;

import org.lunaris.api.entity.EntityType;
import org.lunaris.api.util.math.Vector3d;
import org.lunaris.api.world.Location;
import org.lunaris.block.LBlock;
import org.lunaris.entity.LEntity;
import org.lunaris.entity.misc.Movable;
import org.lunaris.network.packet.Packet28SetEntityMotion;
import org.lunaris.util.math.AxisAlignedBB;
import org.lunaris.util.math.LMath;
import org.lunaris.util.math.Vector3f;
import org.lunaris.world.LWorld;

import java.util.*;

/**
 * Created by RINES on 30.09.17.
 */
public class MovementData implements Movable {

    protected final static float TICK_RATE = .05F;
    protected final static float GRAVITY = 0.08F;

    private final LEntity entity;

    protected float x, y, z;
    protected float motionX, motionY, motionZ;
    protected float yaw, headYaw, pitch;

    protected boolean dirty;
    protected float lastUpdateDt;

    public MovementData(LEntity entity) {
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
    public Location getLocation(LWorld world) {
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

    @Override
    public Vector3d getDirection() {
        double rx = Math.toRadians(getYaw()), ry = Math.toRadians(getPitch());
        double xz = Math.cos(ry);
        return new Vector3d(-xz * Math.sin(rx), -Math.sin(ry), xz * Math.cos(rx));
    }

    public boolean isDirty() {
        boolean result = this.dirty;
        this.dirty = false;
        return result;
    }

    public void tickMovement(long current, float dT) {
        // Check if we need to calc motion
        this.lastUpdateDt += dT;

        // Move by motion amount

        float dX = this.motionX;
        float dY = this.motionY;
        float dZ = this.motionZ;

        if (this.lastUpdateDt >= TICK_RATE) {
            if (!this.entity.getMetadata().getDataFlag(false, EntityDataFlag.IMMOBILE) || true) {
                // Calc motion
                if (this.entity.getMetadata().getDataFlag(false, EntityDataFlag.AFFECTED_BY_GRAVITY)) {
                    this.motionY -= GRAVITY;
                }

                // Check if we are stuck in a block
                checkWhetherInsideBlocks(this.entity.getWorld());

                // Security check so we don't move and collect bounding boxes like crazy
                if (Math.abs(this.motionX) > 20 || Math.abs(this.motionZ) > 20 || Math.abs(this.motionY) > 20) {
                    return;
                }

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

                boolean notFallingFlag = (this.entity.isOnGround() || (dY != this.motionY && this.motionY < 0));
                if (this.entity.getStepHeight() > 0 && notFallingFlag && (this.motionX != dX || this.motionZ != dZ)) {
                    float oldDX = dX;
                    float oldDY = dY;
                    float oldDZ = dZ;

                    dX = this.motionX;
                    dY = this.entity.getStepHeight();
                    dZ = this.motionZ;

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
                    }
                }

                // Move by new bounding box
                if (dX != 0.0 || dY != 0.0 || dZ != 0.0) {
                    AxisAlignedBB bb = this.entity.getBoundingBox();
                    setPosition(
                            (bb.getMinX() + bb.getMaxX()) / 2,
                            bb.getMinY(),
                            (bb.getMinZ() + bb.getMaxZ()) / 2
                    );
                }

                // Check for grounding states
                this.entity.setupCollisionFlags(this.motionX, this.motionY, this.motionZ, dX, dY, dZ);

                // We did not move so we collided, set motion to 0 to escape hell
                if (this.motionX != dX) {
                    this.motionX = 0F;
                }

                if (this.motionY != dY) {
                    this.motionY = 0F;
                }

                if (this.motionZ != dZ) {
                    this.motionZ = 0F;
                }

                // Reset last update
            }
            checkBlockCollisions();
            setupFallDistance(dY);
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

    private void checkBlockCollisions() {
        List<LBlock> blocks = null;
        LWorld world = this.entity.getWorld();
        AxisAlignedBB bb = this.entity.getBoundingBox().grow(.1F, .1F, .1F);
        int minX = LMath.fastFloor(bb.getMinX());
        int minY = LMath.fastFloor(bb.getMinY());
        int minZ = LMath.fastFloor(bb.getMinZ());
        int maxX = LMath.fastCeil(bb.getMaxX());
        int maxY = LMath.fastCeil(bb.getMaxY());
        int maxZ = LMath.fastCeil(bb.getMaxZ());

        for (int z = minZ; z < maxZ; ++z) {
            for (int x = minX; x < maxX; ++x) {
                for (int y = minY; y < maxY; ++y) {
                    LBlock block = world.getBlockAt(x, y, z);
                    AxisAlignedBB blockBox = block.getBoundingBox();
                    if (blockBox.intersectsWith(bb)) {
                        if (blocks == null) {
                            blocks = new ArrayList<>();
                        }
                        blocks.add(block);
                    }
                }
            }
        }
        if (blocks == null) {
            return;
        }
        Vector3f push = new Vector3f();
        for (LBlock block : blocks) {
            block.getHandle().onEntityCollide(block, this.entity);
            block.getHandle().addVelocityToEntity(block, this.entity, push);
        }
        if (push.length() > 0) {
            push = push.normalize().multiply(.014F);
            this.motionX += push.x;
            this.motionY += push.y;
            this.motionZ += push.z;
            this.entity.sendPacketToWatchers(new Packet28SetEntityMotion(this.entity));
        }
    }

    protected void setupFallDistance(float dy) {
        this.entity.setupFallDistance(dy);
    }

    protected void checkWhetherInsideBlocks(LWorld world) {
        if (this.entity.getEntityType() == EntityType.PLAYER)
            return;
        AxisAlignedBB bb = this.entity.getBoundingBox();
        int bx = LMath.fastFloor(this.x);
        int by = LMath.fastFloor(this.y);
        int bz = LMath.fastFloor(this.z);
        LBlock block;
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

    protected static class DirectionPushData {

        private byte direction = -1;
        private float force = 9999F;

        protected void check(boolean valid, int direction, float force) {
            if (valid && force < this.force) {
                this.direction = (byte) direction;
                this.force = force;
            }
        }

        protected void accept(MovementData movement) {
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

    protected static List<AxisAlignedBB> getCollisionCubes(LEntity theEntity, AxisAlignedBB bb, boolean includeEntities) {
        LWorld world = theEntity.getWorld();
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
                    LBlock block = world.getBlockAt(x, y, z);
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
            Collection<LEntity> entities = getNearbyEntities(theEntity, bb.grow(.25F, .25F, .25F));
            if (entities != null)
                for (LEntity entity : entities) {
                    if (collisions == null)
                        collisions = new ArrayList<>();
                    collisions.add(entity.getBoundingBox());
                }
        }
        return collisions;
    }

    protected static Collection<LEntity> getNearbyEntities(LEntity theEntity, AxisAlignedBB bb) {
        LWorld world = theEntity.getWorld();
        Set<LEntity> result = null;
        for (LEntity entity : world.getEntities())
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
