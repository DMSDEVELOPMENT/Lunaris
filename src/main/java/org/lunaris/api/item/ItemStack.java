package org.lunaris.api.item;

import org.lunaris.api.material.BlockHandle;
import org.lunaris.api.material.ItemHandle;
import org.lunaris.api.material.Material;
import org.lunaris.api.material.MaterialHandle;
import org.lunaris.api.util.Internal;
import org.lunaris.item.ItemList;
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

    /**
     * Create new itemstack of given type, amount = 1 and data = 0.
     * @param type the material of itemstack.
     */
    public ItemStack(Material type) {
        this(type, 1, 0);
    }

    /**
     * Create new itemstack of given type and amount; data = 0.
     * @param type the material of itemstack.
     * @param amount amount of itemstack.
     */
    public ItemStack(Material type, int amount) {
        this(type, amount, 0);
    }

    /**
     * Create new itemstack of given type, amount and data.
     * @param type the material of itemstack.
     * @param amount amount of itemstack.
     * @param data data of itemstack.
     */
    public ItemStack(Material type, int amount, int data) {
        this.type = type;
        this.amount = amount;
        this.data = data < 0 ? 0 : data;
    }

    /**
     * Create new itemstack of given type id, amount and data.
     * @param id identifier of material of itemstack.
     * @param amount amount of itemstack.
     * @param data data of itemstack.
     */
    public ItemStack(int id, int amount, int data) {
        this(Material.getById(id), amount, data);
    }

    /**
     * Create new itemstack from minecraftish material string.
     * @param stringData minecrafting material string.
     */
    @Internal
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

    /**
     * Get this item's material.
     * @return this item's material.
     */
    public Material getType() {
        return type;
    }

    /**
     * Get this item's data.
     * @return this item's data.
     */
    public int getData() {
        return this.data;
    }

    /**
     * Set this item's data.
     * @param data new item's data.
     */
    public void setData(int data) {
        this.data = data < 0 ? 0 : data;
    }

    /**
     * Get this item's amount.
     * @return this item's amount.
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Set this item's amount.
     * @param amount new item's amount.
     */
    public void setAmount(int amount) {
        this.amount = Math.max(0, amount);
    }

    /**
     * Get this item's max stack size.
     * @see MaterialHandle#getMaxStackSize(int)
     * @return this item's max stack size.
     */
    public int getMaxStackSize() {
        return getHandle().getMaxStackSize(this.data);
    }

    /**
     * Get this item's tool type.
     * @see ItemHandle#getToolType()
     * @return this item's tool type.
     */
    public ItemToolType getToolType() {
        return isBlock() ? ItemToolType.NONE : getItemHandle().getToolType();
    }

    /**
     * Check whether this item is of given tool type.
     * @param toolType the tool type to check for.
     * @return if this item is of given tool type.
     */
    public boolean isOfToolType(ItemToolType toolType) {
        return toolType == ItemToolType.NONE || toolType == getToolType();
    }

    /**
     * Get this item's tier.
     * @see ItemHandle#getTier()
     * @return this item's tier.
     */
    public ItemTier getTier() {
        return isBlock() ? ItemTier.NONE : getItemHandle().getTier();
    }

    /**
     * Check whether this item is of given tier or higher.
     * @param tier the tier to check for.
     * @return if this item is of given tier or higher.
     */
    public boolean isOfTier(ItemTier tier) {
        return getTier().compareTo(tier) >= 0;
    }

    /**
     * Check whether this item is of given tool type and of given tier or higher.
     * @see ItemStack#isOfToolType(ItemToolType)
     * @see ItemStack#isOfTier(ItemTier)
     * @param toolType the tool type to check for.
     * @param tier the tier to check for.
     * @return if this item is of given tool type and of given tier or higher.
     */
    public boolean isOfToolTier(ItemToolType toolType, ItemTier tier) {
        return isOfToolType(toolType) && isOfTier(tier);
    }

    /**
     * Check whether this itemstack represents an item and not a block.
     * @return if this itemstack represents an item and not a block.
     */
    public boolean isItem() {
        return getHandle().isItem();
    }

    /**
     * Check whether this itemstack represents a block and not an item.
     * @return if this itemstack represents a block and not an item.
     */
    public boolean isBlock() {
        return getHandle().isBlock();
    }

    /**
     * Get block handle of this itemstack.
     * @return block handle of this itemstack. null for items.
     */
    public BlockHandle getBlockHandle() {
        return getHandle().asBlock();
    }

    /**
     * Get item handle of this itemstack.
     * @return item handle of this itemstack. null for blocks.
     */
    public ItemHandle getItemHandle() {
        return getHandle().asItem();
    }

    /**
     * Get material handle of this itemstack.
     * @return material handle of this itemstack.
     */
    public MaterialHandle getHandle() {
        return this.type.getHandle();
    }

    /**
     * Check whether this itemstack contains any compound data.
     * @return if this itemstack contains any compound data.
     */
    public boolean hasCompoundTag() {
        return this.nbtData != null && this.nbtData.length > 0;
    }

    /**
     * Get this itemstack compound data in serialized format.
     * @return this itemstack compound data in serialized (byte array) format.
     */
    @Internal
    public byte[] getNbtData() {
        return this.nbtData;
    }

    /**
     * Set this itemstack compound data in serialized format.
     * @param data the data in byte array format itself.
     */
    @Internal
    public void setNbtData(byte[] data) {
        this.nbtData = data;
        this.compiledNbt = null;
    }

    /**
     * Set new compound tag to this itemstack.
     * @param tag new compound tag.
     */
    @Internal
    public void setCompoundTag(CompoundTag tag) {
        if (tag.isEmpty()) {
            setNbtData(new byte[0]);
            return;
        }
        tag.setName(null);
        this.compiledNbt = tag;
        this.nbtData = serializeNbt(tag);
    }

    /**
     * Get itemstack current compound tag.
     * @return itemstack current compound tag.
     */
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

    /**
     * Check whether this itemstack is available in creative.
     * @return if this itemstack can be obtained from creative inventory.
     */
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

    /**
     * Check whether this itemstack equals to another one without checking amount.
     * @param item another itemstack to compare to.
     * @return if this itemstack equals to another one without checking amount.
     */
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
