package org.lunaris.material.block.liquid;

import org.lunaris.LunarisServer;
import org.lunaris.api.event.block.BlockFromToEvent;
import org.lunaris.api.material.Material;
import org.lunaris.api.world.Block;
import org.lunaris.api.world.BlockFace;
import org.lunaris.block.BUFlag;
import org.lunaris.block.LBlock;
import org.lunaris.world.BlockUpdateType;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author xtrafrancyz
 */
public abstract class LiquidDynamicBlock extends LiquidBlock {
    protected LiquidDynamicBlock(Material type, String name) {
        super(type, name);
    }

    @Override
    public void onBlockAdd(Block block) {
        if (!this.checkForMixing(block)) {
            LBlock lb = (LBlock) block;
            lb.getWorld().scheduleUpdate(lb, this.tickRate(lb));
        }
    }

    @Override
    public void onUpdate(Block b, BlockUpdateType type) {
        LBlock block = (LBlock) b;
        if (type == BlockUpdateType.NORMAL) {
            this.checkForMixing(block);
            block.getWorld().scheduleUpdate(block, this.tickRate(block));
        } else if (type == BlockUpdateType.SCHEDULED) {
            int decay = block.getData();
            int multiplier = block.getType() == Material.LAVA ? 2 : 1;
            int tickRate = tickRate(block);

            if (decay > 0) {
                int smallestFlowDecay = -100;
                int[] adjacentSources = {0};
                for (BlockFace face : BlockFace.Plane.HORIZONTAL)
                    smallestFlowDecay = this.getSmallestFlowDecay(block.getSide(face), adjacentSources, smallestFlowDecay);

                int aaaa = smallestFlowDecay + multiplier;

                if (aaaa >= 8 || smallestFlowDecay < 0) {
                    aaaa = -1;
                }

                int topFlowDecay;
                if ((topFlowDecay = this.getFlowDecay(block.getSide(BlockFace.UP))) >= 0) {
                    if (topFlowDecay >= 8) {
                        aaaa = topFlowDecay;
                    } else {
                        aaaa = topFlowDecay + 8;
                    }
                }

                if (adjacentSources[0] >= 2 && isWater(block.getType())) {
                    LBlock bottomBlock = block.getSide(BlockFace.DOWN);
                    if (bottomBlock.getHandle().isSolid()) {
                        aaaa = 0;
                    } else if (bottomBlock.getType() == getType() && bottomBlock.getData() == 0) {
                        aaaa = 0;
                    }
                }

                if (isLava(block.getType()) && decay < 8 && aaaa < 8 && aaaa > decay && ThreadLocalRandom.current().nextInt(4) != 0) {
                    tickRate *= 4;
                }

                if (aaaa == decay) {
                    this.setStaticBlock(block);
                } else {
                    decay = aaaa;
                    if (decay < 0) {
                        block.setType(Material.AIR);
                    } else {
                        block.getWorld().scheduleUpdate(block, tickRate);
                        block.setData(decay);
                    }
                }
            } else {
                this.setStaticBlock(block);
            }

            LBlock bottomBlock = block.getSide(BlockFace.DOWN);

            if (canFlowInto(bottomBlock)) {
                if (isLava(block.getType()) && isWater(bottomBlock.getType())) {
                    BlockFromToEvent event = new BlockFromToEvent(bottomBlock, new LBlock(bottomBlock.getLocation(), Material.STONE));
                    LunarisServer.getInstance().getEventManager().call(event);
                    if (!event.isCancelled()) {
                        bottomBlock.setType(Material.STONE);
                        this.triggerLavaMixEffects(bottomBlock);
                        return;
                    }
                }

                if (decay >= 8) {
                    this.flowIntoBlock(bottomBlock, decay);
                } else {
                    this.flowIntoBlock(bottomBlock, decay + 8);
                }
            } else if (decay >= 0 && (decay == 0 || !bottomBlock.getHandle().canBeFlowedInto())) {
                Set<BlockFace> set = this.getPossibleFlowDirections(block);
                int l = decay + multiplier;

                if (decay >= 8) {
                    l = 1;
                }

                if (l >= 8) {
                    return;
                }

                for (BlockFace face : set)
                    this.flowIntoBlock(block.getSide(face), l);
            }
        }
    }

    private void setStaticBlock(LBlock block) {
        block.setTypeAndData(getStaticType(), block.getData(), BUFlag.SEND_PACKET);
    }

    private void flowIntoBlock(LBlock block, int newFlowDecay) {
        if (canFlowInto(block)) {
            if (block.getType() != Material.AIR) {
                if (isLava(getType())) {
                    this.triggerLavaMixEffects(block);
                } else {
                    this.dropBlockAsItem(block);
                }
            }

            block.setTypeAndData(getType(), newFlowDecay);
        }
    }

    private int getSmallestFlowDecay(LBlock block, int[] adjacentSources, int decay) {
        int blockDecay = this.getFlowDecay(block);

        if (blockDecay < 0) {
            return decay;
        } else if (blockDecay == 0) {
            adjacentSources[0]++;
        } else if (blockDecay >= 8) {
            blockDecay = 0;
        }

        return (decay >= 0 && blockDecay >= decay) ? decay : blockDecay;
    }

    private int calculateFlowCost(LBlock block, int distance, BlockFace prevFace) {
        int cost = 1000;

        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            if (face != prevFace) {
                LBlock sideBlock = block.getSide(face);
                if (sideBlock.getHandle().canBeFlowedInto() && (!(sideBlock.getHandle() instanceof LiquidBlock) || sideBlock.getData() > 0)) {
                    if (sideBlock.getSide(BlockFace.DOWN).getHandle().canBeFlowedInto())
                        return distance;

                    if (distance < 4) {
                        int j = this.calculateFlowCost(sideBlock, distance + 1, face.getOpposite());

                        if (j < cost)
                            cost = j;
                    }
                }
            }
        }

        return cost;
    }

    protected Set<BlockFace> getPossibleFlowDirections(LBlock block) {
        int minCost = 1000;
        Set<BlockFace> set = EnumSet.noneOf(BlockFace.class);
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            LBlock sideBlock = block.getSide(face);
            if (sideBlock.getHandle().canBeFlowedInto() && (!(sideBlock.getHandle() instanceof LiquidBlock) || sideBlock.getData() > 0)) {
                int cost;

                if (!sideBlock.getSide(BlockFace.DOWN).getHandle().canBeFlowedInto()) {
                    cost = this.calculateFlowCost(sideBlock, 1, face.getOpposite());
                } else {
                    cost = 0;
                }

                if (cost < minCost) {
                    set.clear();
                }

                if (cost <= minCost) {
                    set.add(face);
                    minCost = cost;
                }
            }
        }
        return set;
    }

    private boolean canFlowInto(LBlock block) {
        if (isWater(block.getType()) && getFlowingType() == Material.WATER)
            return false;
        return !isLava(block.getType()) && block.getHandle().canBeFlowedInto();
    }
}
