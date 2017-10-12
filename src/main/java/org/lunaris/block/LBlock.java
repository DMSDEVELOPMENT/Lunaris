package org.lunaris.block;

import org.lunaris.api.item.ItemStack;
import org.lunaris.api.world.Block;
import org.lunaris.api.world.BlockFace;
import org.lunaris.material.LBlockHandle;
import org.lunaris.api.material.Material;
import org.lunaris.util.math.AxisAlignedBB;
import org.lunaris.world.LChunk;
import org.lunaris.api.world.Location;
import org.lunaris.world.LWorld;
import org.lunaris.world.tileentity.LTileEntity;

/**
 * Created by RINES on 13.09.17.
 */
public class LBlock implements Block {
    public static final BUFlag.Set DEFAULT_FLAGS = BUFlag.set(BUFlag.UPDATE_NEIGHBORS, BUFlag.SEND_PACKET);

    private Material type;
    private int data;

    private Location location;

    private AxisAlignedBB boundingBox;

    public LBlock(Location location, Material type) {
        this(location, type, 0);
    }

    public LBlock(Location location, Material type, int data) {
        this.location = location;
        this.type = type;
        this.data = data;
    }

    public void setType(Material type) {
        setTypeAndData(type, 0);
    }

    public void setTypeId(int id) {
        setTypeAndData(Material.getById(id), 0);
    }

    public void setData(int data) {
        setTypeAndData(type, data);
    }

    public void setTypeIdAndData(int id, int data) {
        setTypeAndData(Material.getById(id), data);
    }

    public void setTypeAndData(Material type, int data) {
        setTypeAndData(type, data, DEFAULT_FLAGS);
    }

    public void setTypeAndData(Material type, int data, BUFlag... flags) {
        setTypeAndData(type, data, BUFlag.set(flags));
    }

    public void setTypeAndData(Material type, int data, BUFlag.Set flags) {
        if (type == this.type && this.data == data)
            return;
        boolean typeChanged = false;
        if (type != this.type) {
            getHandle().onBreak(ItemStack.AIR, this);
            this.type = type;
            typeChanged = true;
        }
        this.data = data;
        getWorld().updateBlock(this, flags);
        if (typeChanged)
            getHandle().onBlockAdd(this);
    }

    public Material getType() {
        return this.type;
    }

    public LBlockHandle getHandle() {
        return (LBlockHandle) this.type.getHandle();
    }

    public int getTypeId() {
        return this.type.getId();
    }

    public int getData() {
        return this.data;
    }

    public Location getLocation() {
        return this.location.clone();
    }

    public LWorld getWorld() {
        return (LWorld) this.location.getWorld();
    }

    public int getX() {
        return this.location.getBlockX();
    }

    public int getY() {
        return this.location.getBlockY();
    }

    public int getZ() {
        return this.location.getBlockZ();
    }

    public LChunk getChunk() {
        return getWorld().getChunkAt(getX() >> 4, getZ() >> 4);
    }

    public LBlock getSide(BlockFace face) {
        return this.getSide(face, 1);
    }

    public LBlock getSide(BlockFace face, int step) {
        return getWorld().getBlockAt(this.location.getSide(face.getIndex(), step));
    }

    public LBlock getRelative(int x, int y, int z) {
        return getWorld().getBlockAt(getX() + x, getY() + y, getZ() + z);
    }

    public AxisAlignedBB getBoundingBox() {
        if(this.boundingBox == null)
            this.boundingBox = getHandle().recalculateBoundingBox(this);
        return this.boundingBox;
    }

    public LTileEntity getTileEntity() {
        return getWorld().getTileEntityAt(this.location);
    }

    @Override
    public String toString() {
        return "Block(world=" + getWorld() + ", x=" + getX() + ", y=" + getY() + ", z=" + getZ() + ", type=" + type + ":" + data + ")";
    }
}
