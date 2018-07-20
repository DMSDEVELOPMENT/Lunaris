package org.lunaris.material.block;

import org.lunaris.api.entity.Player;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.item.ItemToolType;
import org.lunaris.api.material.Material;
import org.lunaris.api.world.Block;
import org.lunaris.api.world.BlockFace;
import org.lunaris.block.BlockColor;
import org.lunaris.block.LBlock;
import org.lunaris.entity.LPlayer;
import org.lunaris.util.math.AxisAlignedBB;
import org.lunaris.world.LWorld;
import org.lunaris.world.tileentity.ChestTileEntity;
import org.lunaris.world.tileentity.LTileEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RINES on 12.10.17.
 */
public class BlockChest extends TransparentBlock {

    public BlockChest() {
        super(Material.CHEST, "Chest");
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 2.5;
    }

    @Override
    public double getResistance() {
        return 12.5;
    }

    @Override
    public ItemToolType getRequiredToolType() {
        return ItemToolType.AXE;
    }

    @Override
    public AxisAlignedBB recalculateBoundingBox(Block block) {
        return new AxisAlignedBB(
                block.getX() + 0.0625F,
                block.getY(),
                block.getZ() + 0.0625F,
                block.getX() + 0.9375F,
                block.getY() + 0.9475F,
                block.getZ() + 0.9375F
        );
    }

    @Override
    public boolean place(ItemStack stack, Block block, Block against, BlockFace blockFace, double fx, double fy, double fz, Player player) {
        int[] faces = {2, 5, 3, 4};
        int data = faces[player == null ? 0 : ((LPlayer) player).getTargetBlockFace().getHorizontalIndex()];
        //check for parity
        block.setTypeAndData(getType(), data);
        return true;
    }

    @Override
    public void onBlockAdd(Block block) {
        ((LWorld) block.getWorld()).registerTileEntity(new ChestTileEntity(block.getLocation()));
    }

    @Override
    public boolean onBreak(ItemStack item, Block block) {
        LTileEntity tileEntity = ((LBlock) block).getTileEntity();
        if (tileEntity == null)
            return true;
        ((LWorld) block.getWorld()).unregisterTileEntity(tileEntity);
        return true;
    }

    @Override
    public boolean onActivate(Block block, ItemStack item, Player player) {
        ChestTileEntity tileEntity = (ChestTileEntity) ((LBlock) block).getTileEntity();
        if (tileEntity == null) {
            tileEntity = new ChestTileEntity(block.getLocation());
            ((LWorld) block.getWorld()).registerTileEntity(tileEntity);
        }
        ((LPlayer) player).openInventory(tileEntity.getInventory());
        return true;
    }

    @Override
    public List<ItemStack> getDrops(Block block, ItemStack hand) {
        List<ItemStack> items = new ArrayList<>();
        items.add(new ItemStack(Material.CHEST));
        ChestTileEntity tileEntity = (ChestTileEntity) ((LBlock) block).getTileEntity();
        if (tileEntity == null)
            return items;
        for (ItemStack is : tileEntity.getInventory())
            if (is != null && is.getType() != Material.AIR)
                items.add(is);
        return items;
    }

    @Override
    public BlockColor getColor(int data) {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

}
