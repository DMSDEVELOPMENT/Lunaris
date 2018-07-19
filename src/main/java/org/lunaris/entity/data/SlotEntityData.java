package org.lunaris.entity.data;

import org.lunaris.api.item.ItemStack;
import org.lunaris.api.material.Material;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SlotEntityData extends EntityData<ItemStack> {
    public int blockId;
    public int meta;
    public int count;

    public SlotEntityData(int id, int blockId, int meta, int count) {
        super(id);
        this.blockId = blockId;
        this.meta = meta;
        this.count = count;
    }

    public SlotEntityData(int id, ItemStack item) {
        this(id, item.getType().getId(), (byte) item.getData(), item.getAmount());
    }

    @Override
    public ItemStack getData() {
        return new ItemStack(Material.getById(blockId), count, meta);
    }

    @Override
    public void setData(ItemStack data) {
        this.blockId = data.getType().getId();
        this.meta = data.getData();
        this.count = data.getAmount();
    }

    @Override
    public EntityDataType getType() {
        return EntityDataType.SLOT;
    }
}
