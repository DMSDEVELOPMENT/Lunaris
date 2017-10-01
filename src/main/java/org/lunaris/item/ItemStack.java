package org.lunaris.item;

import org.lunaris.block.Block;
import org.lunaris.entity.Entity;
import org.lunaris.material.ItemMaterial;
import org.lunaris.material.SpecifiedMaterial;
import org.lunaris.material.Material;
import org.lunaris.nbt.NBTIO;
import org.lunaris.nbt.tag.CompoundTag;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.regex.Pattern;

/**
 * Created by RINES on 13.09.17.
 */
public class ItemStack {

    public final static ItemStack AIR = new ItemStack(Material.AIR, 1);

    private final Material material;
    private int data;
    private int amount;
    private byte[] nbtData;
    private CompoundTag compiledNbt;

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

    public ItemStack(String stringData) {
        String[] b = stringData.trim().replace(' ', '_').replace("minecraft:", "").split(":");

        int id = 0, data = 0;

        Pattern pattern = Pattern.compile("^[1-9]\\d*$");
        if(pattern.matcher(b[0]).matches())
            id = Integer.parseInt(b[0]);
        else try {
            id = ItemList.class.getField(b[0].toUpperCase()).getInt(null);
        }catch(Exception ignored) {}
        id &= 0xffff;
        if(b.length != 1)
            data = Integer.parseInt(b[1]) & 0xffff;
        this.material = Material.getById(id);
        this.amount = 1;
        this.data = data;
    }

    public Material getMaterial() {
        return this.material;
    }

    public Material getType() {
        return getMaterial();
    }

    public int getData() {
        return this.data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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
        if(tag.isEmpty()) {
            setNbtData(new byte[0]);
            return;
        }
        tag.setName(null);
        this.compiledNbt = tag;
        this.nbtData = serializeNbt(tag);
    }

    public CompoundTag getCompoundTag() {
        if(!hasCompoundTag())
            return null;
        if(this.compiledNbt == null)
            this.compiledNbt = deserializeNbt(this.nbtData);
        if(this.compiledNbt != null)
            this.compiledNbt.setName("");
        return this.compiledNbt;
    }

    private SpecifiedMaterial getSpecifiedMaterial() {
        return this.material.getSpecifiedMaterial();
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

}
