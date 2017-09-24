package org.lunaris.material;

import org.lunaris.block.Block;
import org.lunaris.block.BlockColor;
import org.lunaris.entity.Entity;
import org.lunaris.entity.Player;
import org.lunaris.item.ItemStack;
import org.lunaris.item.ItemTier;
import org.lunaris.item.ItemToolType;
import org.lunaris.util.math.AxisAlignedBB;

/**
 * Created by RINES on 24.09.17.
 */
public class BlockMaterial extends SpecifiedMaterial {

    protected BlockMaterial(Material material, String name) {
        super(material, name);
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

    public int tickRate() {
        return 10;
    }

    public boolean onBreak(ItemStack item, Block block) {
//        return this.getLevel().setBlock(this, new BlockAir(), true, true);
        return true;
    }

    public int onUpdate(int type) {
        return 0;
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
        return isBlock() ? ItemToolType.NONE : null;
    }

    @Override
    public final ItemToolType getToolType() {
        return ItemToolType.NONE;
    }

    @Override
    public final ItemTier getTier() {
        return ItemTier.NONE;
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
        if(block.getBoundingBox() == null)
            block.setBoundingBox(recalculateBoundingBox(block));
        return block.getBoundingBox();
    }

    public AxisAlignedBB getCollisionBoundingBox(Block block) {
        if(block.getCollisionBoundingBox() == null)
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

    public void update(Block block) {

    }

    public ItemStack[] getDrops(Block block, ItemStack hand) {
        return new ItemStack[0];
    }

    public BlockColor getColor() {
        return BlockColor.VOID_BLOCK_COLOR;
    }

}
