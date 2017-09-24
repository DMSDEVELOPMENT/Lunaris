package org.lunaris.item;

import org.lunaris.block.Block;
import org.lunaris.entity.Entity;
import org.lunaris.material.ItemMaterial;
import org.lunaris.material.SpecifiedMaterial;
import org.lunaris.material.Material;

/**
 * Created by RINES on 13.09.17.
 */
public class ItemStack {

    public final static ItemStack AIR = new ItemStack(Material.AIR, 1);

    private final Material material;
    private int data;
    private int amount;

    public ItemStack(Material material) {
        this(material, 0, 0);
    }

    public ItemStack(Material material, int amount) {
        this(material, amount, 0);
    }

    public ItemStack(Material material, int amount, int data) {
        this.material = material;
        this.amount = amount;
        this.data = data;
    }

    public Material getMaterial() {
        return material;
    }

    public Material getType() {
        return getMaterial();
    }

    public int getData() {
        return data;
    }

    public int getAmount() {
        return amount;
    }

    public ItemToolType getToolType() {
        return getSpecifiedMaterial().getToolType();
    }

    public boolean isOfToolType(ItemToolType toolType) {
        return toolType == ItemToolType.NONE || toolType == getToolType();
    }

    public ItemTier getTier() {
        return getSpecifiedMaterial().getTier();
    }

    public boolean isOfTier(ItemTier tier) {
        return getTier().compareTo(tier) >= 0;
    }

    public boolean useOn(Block block, Entity user) {
        SpecifiedMaterial material = getSpecifiedMaterial();
        if(material.isBlock())
            return false;
        return ((ItemMaterial) material).useOn(block, user);
    }

    public boolean useOn(Entity entity, Entity user) {
        SpecifiedMaterial material = getSpecifiedMaterial();
        if(material.isBlock())
            return false;
        return ((ItemMaterial) material).useOn(entity, user);
    }

    private SpecifiedMaterial getSpecifiedMaterial() {
        return this.material.getSpecifiedMaterial();
    }

}
