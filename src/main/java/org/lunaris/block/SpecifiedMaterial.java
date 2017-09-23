package org.lunaris.block;

import org.lunaris.entity.Player;
import org.lunaris.item.ItemStack;
import org.lunaris.item.ItemTier;
import org.lunaris.item.ItemToolType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by RINES on 13.09.17.
 */
public abstract class SpecifiedMaterial {

    private final static Map<Material, SpecifiedMaterial> BLOCK_MATERIALS = new EnumMap<>(Material.class);

    static {
        //preload block materials
    }

    public static SpecifiedMaterial getByMaterial(Material material) {
        return BLOCK_MATERIALS.get(material);
    }

    private final Material material;
    private final String name;

    protected SpecifiedMaterial(Material material, String name) {
        this.material = material;
        this.name = name;
        BLOCK_MATERIALS.put(material, this);
    }

    public Material getMaterial() {
        return this.material;
    }

    public int getId() {
        return this.material.getId();
    }

    public String getName() {
        return this.name;
    }

    public boolean isBlock() {
        return getId() < 256;
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

    public boolean onActivate(ItemStack item) {
        return this.onActivate(item, null);
    }

    public boolean onActivate(ItemStack item, Player player) {
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

    public ItemToolType getToolType() {
        return ItemToolType.NONE;
    }

    public ItemTier getTier() {
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

    public BlockColor getColor() {
        return BlockColor.VOID_BLOCK_COLOR;
    }

}
