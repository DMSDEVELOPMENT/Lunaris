package org.lunaris.material.block.liquid;

import org.lunaris.LunarisServer;
import org.lunaris.api.entity.Entity;
import org.lunaris.api.event.block.BlockFromToEvent;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.material.Material;
import org.lunaris.api.util.math.Vector3d;
import org.lunaris.api.world.Block;
import org.lunaris.api.world.BlockFace;
import org.lunaris.api.world.Sound;
import org.lunaris.block.LBlock;
import org.lunaris.entity.LEntity;
import org.lunaris.entity.LPlayer;
import org.lunaris.material.block.TransparentBlock;
import org.lunaris.util.math.AxisAlignedBB;
import org.lunaris.util.math.Vector3f;
import org.lunaris.world.particle.GenericParticle;
import org.lunaris.world.particle.ParticleType;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author xtrafrancyz
 */
public abstract class LiquidBlock extends TransparentBlock {
    protected LiquidBlock(Material type, String name) {
        super(type, name);
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public boolean isBreakable(ItemStack item) {
        return false;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public AxisAlignedBB recalculateBoundingBox(Block block) {
        return new AxisAlignedBB(block.getX(), block.getY(), block.getZ(), block.getX() + 1, block.getY() + 1 - getFluidHeightPercent(block), block.getZ() + 1);
    }

    @Override
    public double getHardness() {
        return 100;
    }

    @Override
    public double getResistance() {
        return 500;
    }

    @Override
    public void addVelocityToEntity(Block block, Entity entity, Vector3f vector) {
        Vector3d flow = this.getFlowVector(block);
        vector.x += flow.x;
        vector.y += flow.y;
        vector.z += flow.z;
    }

    @Override
    public void onNeighborBlockChange(Block block, Block neighborBlock) {
        checkForMixing(block);
    }

    @Override
    public void onBlockAdd(Block block) {
        checkForMixing(block);
    }

    @Override
    public void onEntityCollide(Block block, Entity entity) {
        ((LEntity) entity).setFallDistance(0);
    }

    protected boolean isLava(Material type) {
        return type == Material.LAVA || type == Material.LAVA_STILL;
    }

    protected boolean isWater(Material type) {
        return type == Material.WATER || type == Material.WATER_STILL;
    }

    public float getFluidHeightPercent(Block block) {
        float d = (float) block.getData();
        if (d >= 8)
            d = 0;
        return (d + 1) / 9f;
    }

    protected int getFlowDecay(Block block) {
        return block.getHandle() instanceof LiquidBlock ? block.getData() : -1;
    }

    protected int getEffectiveFlowDecay(Block block) {
        if (block.getType() != getType())
            return -1;

        int decay = block.getData();
        if (decay >= 8)
            decay = 0;
        return decay;
    }

    protected Vector3d getFlowVector(Block b) {
        LBlock block = (LBlock) b;
        Vector3d vector = new Vector3d();
        int decay = this.getEffectiveFlowDecay(block);

        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            LBlock sideBlock = block.getSide(face);
            int blockDecay = this.getEffectiveFlowDecay(sideBlock);

            if (blockDecay < 0) {
                if (!sideBlock.getHandle().canBeFlowedInto())
                    continue;

                blockDecay = this.getEffectiveFlowDecay(sideBlock.getSide(BlockFace.DOWN));

                if (blockDecay >= 0) {
                    int realDecay = blockDecay - (decay - 8);
                    vector.x += (sideBlock.getX() - block.getX()) * realDecay;
                    vector.y += (sideBlock.getY() - block.getY()) * realDecay;
                    vector.z += (sideBlock.getZ() - block.getZ()) * realDecay;
                }
            } else {
                int realDecay = blockDecay - decay;
                vector.x += (sideBlock.getX() - block.getX()) * realDecay;
                vector.y += (sideBlock.getY() - block.getY()) * realDecay;
                vector.z += (sideBlock.getZ() - block.getZ()) * realDecay;
            }
        }

        if (block.getData() >= 8) {
            boolean falling = false;

            if (!block.getRelative(0, 0, -1).getHandle().canBeFlowedInto()) {
                falling = true;
            } else if (!block.getRelative(0, 0, 1).getHandle().canBeFlowedInto()) {
                falling = true;
            } else if (!block.getRelative(-1, 0, 0).getHandle().canBeFlowedInto()) {
                falling = true;
            } else if (block.getRelative(1, 0, 0).getHandle().canBeFlowedInto()) {
                falling = true;
            } else if (!block.getRelative(0, 1, -1).getHandle().canBeFlowedInto()) {
                falling = true;
            } else if (!block.getRelative(0, 1, 1).getHandle().canBeFlowedInto()) {
                falling = true;
            } else if (!block.getRelative(-1, 1, 0).getHandle().canBeFlowedInto()) {
                falling = true;
            } else if (block.getRelative(1, 1, 0).getHandle().canBeFlowedInto()) {
                falling = true;
            }

            if (falling) {
                vector = vector.normalize().add(0, -6, 0);
            }
        }

        return vector.normalize();
    }

    protected boolean checkForMixing(Block block) {
        if (isLava(block.getType())) {
            boolean colliding = false;
            for (BlockFace face : BlockFace.values()) {
                if (colliding = isWater(block.getSide(face).getType())) {
                    break;
                }
            }

            if (colliding) {
                Material to;
                if (block.getData() == 0)
                    to = Material.OBSIDIAN;
                else if (block.getData() <= 4)
                    to = Material.COBBLESTONE;
                else
                    return false;

                BlockFromToEvent event = new BlockFromToEvent(block, new LBlock(block.getLocation(), to));
                LunarisServer.getInstance().getEventManager().call(event);
                if (!event.isCancelled()) {
                    block.setType(to);
                    this.triggerLavaMixEffects(block);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates fizzing sound and smoke. Used when lava flows over block or mixes with water.
     */
    protected void triggerLavaMixEffects(Block b) {
        LBlock block = (LBlock) b;
        float pitch = 2.6F + (ThreadLocalRandom.current().nextFloat() - ThreadLocalRandom.current().nextFloat()) * 0.8F;
        block.getWorld().playSound(Sound.FIZZ, block.getLocation().add(0.5, 0.5, 0.5), pitch);

        Collection<LPlayer> players = block.getWorld().getWatcherPlayers(block.getLocation());
        for (int i = 0; i < 8; ++i)
            new GenericParticle(ParticleType.SMOKE, block.getLocation().add(Math.random(), 1.2, Math.random())).send(players);
    }

    public Material getFlowingType() {
        switch (getType()) {
            case WATER:
            case WATER_STILL:
                return Material.WATER;
            case LAVA:
            case LAVA_STILL:
                return Material.LAVA;
            default:
                return Material.AIR;
        }
    }

    public Material getStaticType() {
        switch (getType()) {
            case WATER:
            case WATER_STILL:
                return Material.WATER_STILL;
            case LAVA:
            case LAVA_STILL:
                return Material.LAVA_STILL;
            default:
                return Material.AIR;
        }
    }
}
