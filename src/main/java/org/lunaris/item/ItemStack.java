package org.lunaris.item;

import org.lunaris.block.Material;

/**
 * Created by RINES on 13.09.17.
 */
public class ItemStack {

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

    public int getData() {
        return data;
    }

    public int getAmount() {
        return amount;
    }

}
