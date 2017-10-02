package org.lunaris.material.block;

import org.lunaris.Lunaris;
import org.lunaris.block.Block;
import org.lunaris.block.BlockFace;
import org.lunaris.event.block.BlockFromToEvent;
import org.lunaris.material.BlockMaterial;
import org.lunaris.material.Material;
import org.lunaris.world.BlockUpdateType;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author xtrafrancyz
 */
public abstract class LiquidDynamicBlock extends LiquidBlock {
    protected LiquidDynamicBlock(Material material, String name) {
        super(material, name);
    }

    @Override
    public void onBlockAdd(Block block) {
        //System.out.println(getType() + " add");
        if (!this.checkForMixing(block))
            block.getWorld().scheduleUpdate(block, this.tickRate(block));
    }

    @Override
    public void onUpdate(Block block, BlockUpdateType type) {
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

                if (adjacentSources[0] >= 2 && block.getType() == Material.WATER) {
                    Block bottomBlock = block.getSide(BlockFace.DOWN);
                    if (bottomBlock.getSpecifiedMaterial().isSolid()) {
                        aaaa = 0;
                    } else if (bottomBlock.getType() == getType() && bottomBlock.getData() == 0) {
                        aaaa = 0;
                    }
                }

                if (block.getType() == Material.LAVA && decay < 8 && aaaa < 8 && aaaa > decay && ThreadLocalRandom.current().nextInt(4) != 0) {
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

            Block bottomBlock = block.getSide(BlockFace.DOWN);
            BlockMaterial bottomSpecifiedMaterial = bottomBlock.getSpecifiedMaterial();

            if (bottomSpecifiedMaterial.canBeFlowedInto()) {
                if (block.getType() == Material.LAVA && bottomBlock.getType() == Material.WATER) {
                    BlockFromToEvent event = new BlockFromToEvent(bottomBlock, new Block(bottomBlock.getLocation(), Material.STONE));
                    Lunaris.getInstance().getEventManager().call(event);
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
            } else if (decay >= 0 && (decay == 0 || !bottomSpecifiedMaterial.canBeFlowedInto())) {
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
    
    private void setStaticBlock(Block block) {
        block.getChunk().setBlock(block.getLocation(), getStaticType(), block.getData());
    }

    private void flowIntoBlock(Block block, int newFlowDecay) {
        if (block.getSpecifiedMaterial().canBeFlowedInto()) {
            if (block.getTypeId() > 0) {
                if (getType() == Material.LAVA) {
                    this.triggerLavaMixEffects(block);
                } else {
                    this.dropBlockAsItem(block);
                }
            }

            block.setTypeAndData(getType(), newFlowDecay);
        }
    }

    private int getSmallestFlowDecay(Block block, int[] adjacentSources, int decay) {
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

    private int calculateFlowCost(Block block, int distance, BlockFace prevFace) {
        int cost = 1000;

        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            if (face != prevFace) {
                Block sideBlock = block.getSide(face);
                if (sideBlock.getSpecifiedMaterial().canBeFlowedInto() && (!(sideBlock.getSpecifiedMaterial() instanceof LiquidBlock) || sideBlock.getData() > 0)) {
                    if (sideBlock.getSide(BlockFace.DOWN).getSpecifiedMaterial().canBeFlowedInto())
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

    protected Set<BlockFace> getPossibleFlowDirections(Block block) {
        int minCost = 1000;
        Set<BlockFace> set = EnumSet.noneOf(BlockFace.class);
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            Block sideBlock = block.getSide(face);
            if (sideBlock.getSpecifiedMaterial().canBeFlowedInto() && (!(sideBlock.getSpecifiedMaterial() instanceof LiquidBlock) || sideBlock.getData() > 0)) {
                int cost;

                if (!sideBlock.getSide(BlockFace.DOWN).getSpecifiedMaterial().canBeFlowedInto()) {
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
}
