package org.lunaris.inventory;

import org.lunaris.item.ItemStack;

import java.util.Iterator;

/**
 * Created by RINES on 16.09.17.
 */
public class Inventory implements Iterable<ItemStack> {

    private final ItemStack[] items;
    private final InventoryType type;
    private final String title;
    private int maxStackSize = 64;

    Inventory(InventoryType type, String title) {
        this.items = new ItemStack[type.getSize()];
        this.type = type;
        this.title = title == null ? type.getTitle() : title;
    }

    public InventoryType getType() {
        return this.type;
    }

    public String getTitle() {
        return this.title;
    }

    public int size() {
        return this.items.length;
    }

    public int getSize() {
        return size();
    }

    public int getMaxStackSize() {
        return this.maxStackSize;
    }

    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return new Iterator<ItemStack>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return this.index < items.length;
            }

            @Override
            public ItemStack next() {
                return items[this.index++];
            }

        };
    }

}
