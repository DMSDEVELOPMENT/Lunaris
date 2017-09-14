package org.lunaris.block;

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

    public Material getMaterial() {
        return this.material;
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

}
