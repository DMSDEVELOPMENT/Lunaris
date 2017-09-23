package org.lunaris.block;

import org.lunaris.world.Chunk;
import org.lunaris.world.Location;
import org.lunaris.world.World;

/**
 * Created by RINES on 13.09.17.
 */
public class Block {

    private Material material;
    private int data;

    private Location location;

    public Block(Location location, Material material, int data) {
        this.location = location;
        this.material = material;
        this.data = data;
    }

    public void setMaterial(Material type) {
        this.material = type;
        this.data = 0;
        getWorld().updateBlock(this);
    }

    public void setMaterial(int id) {
        setMaterial(Material.getById(id));
    }

    public void setType(Material type) {
        setMaterial(type);
    }

    public void setType(int id) {
        setMaterial(id);
    }

    public void setData(int data) {
        this.data = 0;
        getWorld().updateBlock(this);
    }

    public Material getMaterial() {
        return this.material;
    }

    public Material getType() {
        return this.material;
    }

    public SpecifiedMaterial getSpecifiedMaterial() {
        return SpecifiedMaterial.getByMaterial(this.material);
    }

    public int getId() {
        return this.material.getId();
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

}
