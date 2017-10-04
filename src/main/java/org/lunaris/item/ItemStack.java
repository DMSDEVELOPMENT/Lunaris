package org.lunaris.item;

import org.lunaris.material.BlockHandle;
import org.lunaris.material.ItemHandle;
import org.lunaris.material.Material;
import org.lunaris.material.MaterialHandle;
import org.lunaris.nbt.NBTIO;
import org.lunaris.nbt.tag.CompoundTag;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.regex.Pattern;

/**
 * Created by RINES on 13.09.17.
 */
public class ItemStack implements Cloneable {

    public final static ItemStack AIR = new ItemStack(Material.AIR, 1);

    private final Material type;
    private int data;
    private int amount;
    private byte[] nbtData = new byte[0];
    private CompoundTag compiledNbt;

    public ItemStack(Material type) {
        this(type, 1, 0);
    }

    public ItemStack(Material type, int amount) {
        this(type, amount, 0);
    }

    public ItemStack(Material type, int amount, int data) {
        this.type = type;
        this.amount = amount;
        this.data = data < 0 ? 0 : data;
    }

    public ItemStack(int id, int amount, int data) {
        this(Material.getById(id), amount, data);
    }

    public ItemStack(String stringData) {
        String[] b = stringData.trim().replace(' ', '_').replace("minecraft:", "").split(":");

        int id = 0, data = 0;

        Pattern pattern = Pattern.compile("^[1-9]\\d*$");
        if (pattern.matcher(b[0]).matches())
            id = Integer.parseInt(b[0]);
        else
            try {
                id = ItemList.class.getField(b[0].toUpperCase()).getInt(null);
            } catch (Exception ignored) {}
        id &= 0xffff;
        if (b.length != 1)
            data = Integer.parseInt(b[1]) & 0xffff;
        this.type = Material.getById(id);
        this.amount = 1;
        this.data = data < 0 ? 0 : data;
    }

    public Material getType() {
        return type;
    }

    public int getData() {
        return this.data;
    }

    public void setData(int data) {
        this.data = data < 0 ? 0 : data;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(0, amount);
    }

    public int getMaxStackSize() {
        return getHandle().getMaxStackSize(data);
    }

    public ItemToolType getToolType() {
        return isBlock() ? ItemToolType.NONE : getItemHandle().getToolType();
    }

    public boolean isOfToolType(ItemToolType toolType) {
        return toolType == ItemToolType.NONE || toolType == getToolType();
    }

    public ItemTier getTier() {
        return isBlock() ? ItemTier.NONE : getItemHandle().getTier();
    }

    public boolean isOfTier(ItemTier tier) {
        return getTier().compareTo(tier) >= 0;
    }

    public boolean isOfToolTier(ItemToolType toolType, ItemTier tier) {
        return isOfToolType(toolType) && isOfTier(tier);
    }

    public boolean isItem() {
        return !getHandle().isBlock();
    }

    public boolean isBlock() {
        return getHandle().isBlock();
    }

    public BlockHandle getBlockHandle() {
        return getHandle().asBlock();
    }

    public ItemHandle getItemHandle() {
        return getHandle().asItem();
    }

    public MaterialHandle getHandle() {
        return this.type.getHandle();
    }

    public boolean hasCompoundTag() {
        return this.nbtData != null && this.nbtData.length > 0;
    }

    public byte[] getNbtData() {
        return this.nbtData;
    }

    public void setNbtData(byte[] data) {
        this.nbtData = data;
        this.compiledNbt = null;
    }

    public void setCompoundTag(CompoundTag tag) {
        if (tag.isEmpty()) {
            setNbtData(new byte[0]);
            return;
        }
        tag.setName(null);
        this.compiledNbt = tag;
        this.nbtData = serializeNbt(tag);
    }

    public CompoundTag getCompoundTag() {
        if (!hasCompoundTag())
            return null;
        if (this.compiledNbt == null)
            this.compiledNbt = deserializeNbt(this.nbtData);
        if (this.compiledNbt != null)
            this.compiledNbt.setName("");
        return this.compiledNbt;
    }

    private byte[] serializeNbt(CompoundTag tag) {
        try {
            tag.setName("");
            return NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CompoundTag deserializeNbt(byte[] data) {
        try {
            return NBTIO.read(data, ByteOrder.LITTLE_ENDIAN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean canBeFoundInCreative() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this || o == null && getType() == Material.AIR)
            return true;
        if (!(o instanceof ItemStack))
            return false;
        ItemStack item = (ItemStack) o;
        if (getType() != item.getType() || getData() != item.getData() || getAmount() != item.getAmount()
            || this.nbtData.length != item.nbtData.length)
            return false;
        for (int i = 0; i < this.nbtData.length; ++i)
            if (this.nbtData[i] != item.nbtData[i])
                return false;
        return true;
    }

    public boolean isSimilar(ItemStack item) {
        if (item == null || item == null && getType() == Material.AIR)
            return true;
        if (getType() != item.getType() || getData() != item.getData() || this.nbtData.length != item.nbtData.length)
            return false;
        for (int i = 0; i < this.nbtData.length; ++i)
            if (this.nbtData[i] != item.nbtData[i])
                return false;
        return true;
    }

    @Override
    public ItemStack clone() {
        try {
            return (ItemStack) super.clone();
        } catch (Exception ignored) {}
        return null;
    }

    @Override
    public String toString() {
        return "ItemStack{Material:" + this.type.name() + ", Data:" + this.data + ", Amount:" + this.amount + "}";
    }

}
