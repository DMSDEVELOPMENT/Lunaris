package org.lunaris.material.block;

import org.lunaris.api.entity.Entity;
import org.lunaris.api.world.Block;
import org.lunaris.block.BlockColor;
import org.lunaris.entity.LEntity;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.item.ItemToolType;
import org.lunaris.api.material.Material;
import org.lunaris.material.block.liquid.FlowableBlock;

import java.util.Collections;
import java.util.List;

/**
 * @author xtrafrancyz
 */
public class BlockCobweb extends FlowableBlock {
    protected BlockCobweb() {
        super(Material.COBWEB, "Cobweb");
    }

    @Override
    public double getHardness() {
        return 4;
    }

    @Override
    public double getResistance() {
        return 20;
    }

    @Override
    public ItemToolType getRequiredToolType() {
        return ItemToolType.SWORD;
    }

    @Override
    public void onEntityCollide(Block block, Entity entity) {
        ((LEntity) entity).setFallDistance(0);
    }

    @Override
    public List<ItemStack> getDrops(Block block, ItemStack hand) {
        if (hand != null && (hand.isOfToolType(ItemToolType.SWORD) || hand.isOfToolType(ItemToolType.SHEARS)))
            return Collections.singletonList(new ItemStack(Material.STRING));
        return Collections.emptyList();
    }

    @Override
    public BlockColor getColor(int data) {
        return BlockColor.CLOTH_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

}
