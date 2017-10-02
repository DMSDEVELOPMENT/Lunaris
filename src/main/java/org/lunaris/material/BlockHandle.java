package org.lunaris.material;

import org.lunaris.block.Block;
import org.lunaris.block.BlockColor;
import org.lunaris.block.BlockFace;
import org.lunaris.entity.Entity;
import org.lunaris.entity.Player;
import org.lunaris.item.ItemStack;
import org.lunaris.item.ItemToolType;
import org.lunaris.util.math.AxisAlignedBB;
import org.lunaris.util.math.Vector3d;
import org.lunaris.world.BlockUpdateType;

import java.util.Collections;
import java.util.List;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockHandle extends MaterialHandle {

    protected BlockHandle(Material type, String name) {
        super(type, name);
    }

    @Override
    public final boolean isBlock() {
        return true;
    }

    //http://minecraft.gamepedia.com/Breaking
    public boolean canHarvestWithHand() {  //used for calculating breaking time
        return true;
    }

    public boolean isBreakable(ItemStack item) {
        return true;
    }

    public int tickRate(Block block) {
        return 10;
    }

    public boolean onBreak(ItemStack item, Block block) {
        getDrops(block, item);
        //        return this.getLevel().setBlock(this, new BlockAir(), true, true);
        return true;
    }

    public void onBlockAdd(Block block) {

    }

    public void dropBlockAsItem(Block block) {
        for (ItemStack drop : getDrops(block, null)) {
            // drop item in world
        }
    }

    public void onNeighborBlockChange(Block block, Block neighborBlock) {

    }

    public void onUpdate(Block block, BlockUpdateType type) {
        
    }

    public boolean onActivate(Block block, ItemStack item) {
        return this.onActivate(block, item, null);
    }

    public boolean onActivate(Block block, ItemStack item, Player player) {
        return false;
    }

    public double getHardness() {
        return 10;
    }

    public double getResistance() {
        return 1;
    }

    public int getBurnChance() {
        return 0;
    }

    public int getBurnAbility() {
        return 0;
    }

    public ItemToolType getRequiredToolType() {
        return ItemToolType.NONE;
    }

    public double getFrictionFactor() {
        return 0.6;
    }

    public int getLightLevel() {
        return 0;
    }

    public boolean canBePlaced() {
        return true;
    }

    public boolean place(ItemStack stack, Block block, Block against, BlockFace blockFace, double fx, double fy, double fz, Player player) {
        block.setTypeAndData(getType(), stack.getData());
        return true;
    }

    public boolean canBeReplaced() {
        return false;
    }

    public boolean isTransparent() {
        return false;
    }

    public boolean isSolid() {
        return true;
    }

    public AxisAlignedBB getBoundingBox(Block block) {
        if (block.getBoundingBox() == null)
            block.setBoundingBox(recalculateBoundingBox(block));
        return block.getBoundingBox();
    }

    public AxisAlignedBB getCollisionBoundingBox(Block block) {
        if (block.getCollisionBoundingBox() == null)
            block.setCollisionBoundingBox(recalculateCollisionBoundingBox(block));
        return block.getCollisionBoundingBox();
    }

    protected AxisAlignedBB recalculateBoundingBox(Block block) {
        return new AxisAlignedBB(
            block.getX(),
            block.getY(),
            block.getZ(),
            block.getX() + 1,
            block.getY() + 1,
            block.getZ() + 1
        );
    }

    protected AxisAlignedBB recalculateCollisionBoundingBox(Block block) {
        return getBoundingBox(block);
    }

    public boolean canBeFlowedInto() {
        return false;
    }

    public boolean canBeActivated() {
        return false;
    }

    public boolean hasEntityCollision() {
        return false;
    }

    public boolean canPassThrough() {
        return false;
    }

    public boolean canBePushed() {
        return true;
    }

    public boolean hasComparatorInputOverride() {
        return false;
    }

    public int getComparatorInputOverride() {
        return 0;
    }

    public boolean canBeClimbed() {
        return false;
    }

    public void onEntityCollide(Block block, Entity entity) {

    }

    public void addVelocityToEntity(Block block, Entity entity, Vector3d vector) {

    }

    public List<ItemStack> getDrops(Block block, ItemStack hand) {
        return Collections.emptyList();
    }

    public BlockColor getColor(int data) {
        return BlockColor.VOID_BLOCK_COLOR;
    }

}
