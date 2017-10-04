package org.lunaris.block;

import org.lunaris.material.BlockHandle;
import org.lunaris.material.Material;
import org.lunaris.util.math.AxisAlignedBB;
import org.lunaris.world.Chunk;
import org.lunaris.world.Location;
import org.lunaris.world.World;

/**
 * Created by RINES on 13.09.17.
 */
public class Block {
    public static final BUFlag.Set DEFAULT_FLAGS = BUFlag.set(BUFlag.UPDATE_NEIGHBORS, BUFlag.SEND_PACKET);

    private Material type;
    private int data;

    private Location location;

    private AxisAlignedBB boundingBox;

    public Block(Location location, Material type) {
        this(location, type, 0);
    }

    public Block(Location location, Material type, int data) {
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
            getHandle().onBreak(null, this);
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

    public BlockHandle getHandle() {
        return (BlockHandle) this.type.getHandle();
    }

    public int getTypeId() {
        return this.type.getId();
    }

    public int getData() {
        return this.data;
    }

    public Location getLocation() {
        return this.location;
    }

    public World getWorld() {
        return this.location.getWorld();
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

    public Chunk getChunk() {
        return getWorld().getChunkAt(getX() >> 4, getZ() >> 4);
    }

    public Block getSide(BlockFace face) {
        return this.getSide(face, 1);
    }

    public Block getSide(BlockFace face, int step) {
        return getWorld().getBlockAt(this.location.getSide(face.getIndex(), step));
    }

    public Block getRelative(int x, int y, int z) {
        return getWorld().getBlockAt(getX() + x, getY() + y, getZ() + z);
    }

    public AxisAlignedBB getBoundingBox() {
        if(this.boundingBox == null)
            this.boundingBox = getHandle().recalculateBoundingBox(this);
        return this.boundingBox;
    }

    @Override
    public String toString() {
        return "Block(world=" + getWorld() + ", x=" + getX() + ", y=" + getY() + ", z=" + getZ() + ", type=" + type + ":" + data + ")";
    }
}
