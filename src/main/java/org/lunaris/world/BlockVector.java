package org.lunaris.world;

import org.lunaris.block.BlockFace;
import org.lunaris.util.math.Vector3d;

/**
 * Created by RINES on 14.09.17.
 */
public class BlockVector implements Cloneable {
    public int x;
    public int y;
    public int z;

    public BlockVector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockVector() {
    }

    public BlockVector setComponents(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public Vector3d add(double x) {
        return this.add(x, 0, 0);
    }

    public Vector3d add(double x, double y) {
        return this.add(x, y, 0);
    }

    public Vector3d add(double x, double y, double z) {
        return new Vector3d(this.x + x, this.y + y, this.z + z);
    }

    public Vector3d add(Vector3d x) {
        return new Vector3d(this.x + x.getX(), this.y + x.getY(), this.z + x.getZ());
    }

    public Vector3d subtract(double x) {
        return this.subtract(x, 0, 0);
    }

    public Vector3d subtract(double x, double y) {
        return this.subtract(x, y, 0);
    }

    public Vector3d subtract(double x, double y, double z) {
        return this.add(-x, -y, -z);
    }

    public Vector3d subtract(Vector3d x) {
        return this.add(-x.getX(), -x.getY(), -x.getZ());
    }

    public BlockVector add(int x) {
        return this.add(x, 0, 0);
    }

    public BlockVector add(int x, int y) {
        return this.add(x, y, 0);
    }

    public BlockVector add(int x, int y, int z) {
        return new BlockVector(this.x + x, this.y + y, this.z + z);
    }

    public BlockVector add(BlockVector x) {
        return new BlockVector(this.x + x.getX(), this.y + x.getY(), this.z + x.getZ());
    }

    public BlockVector subtract() {
        return this.subtract(0, 0, 0);
    }

    public BlockVector subtract(int x) {
        return this.subtract(x, 0, 0);
    }

    public BlockVector subtract(int x, int y) {
        return this.subtract(x, y, 0);
    }

    public BlockVector subtract(int x, int y, int z) {
        return this.add(-x, -y, -z);
    }

    public BlockVector subtract(BlockVector x) {
        return this.add(-x.getX(), -x.getY(), -x.getZ());
    }

    public BlockVector multiply(int number) {
        return new BlockVector(this.x * number, this.y * number, this.z * number);
    }

    public BlockVector divide(int number) {
        return new BlockVector(this.x / number, this.y / number, this.z / number);
    }

    public BlockVector getSide(BlockFace face) {
        return this.getSide(face, 1);
    }

    public BlockVector getSide(BlockFace face, int step) {
        return new BlockVector(this.getX() + face.getXOffset() * step, this.getY() + face.getYOffset() * step, this.getZ() + face.getZOffset() * step);
    }

    public BlockVector up() {
        return up(1);
    }

    public BlockVector up(int step) {
        return getSide(BlockFace.UP, step);
    }

    public BlockVector down() {
        return down(1);
    }

    public BlockVector down(int step) {
        return getSide(BlockFace.DOWN, step);
    }

    public BlockVector north() {
        return north(1);
    }

    public BlockVector north(int step) {
        return getSide(BlockFace.NORTH, step);
    }

    public BlockVector south() {
        return south(1);
    }

    public BlockVector south(int step) {
        return getSide(BlockFace.SOUTH, step);
    }

    public BlockVector east() {
        return east(1);
    }

    public BlockVector east(int step) {
        return getSide(BlockFace.EAST, step);
    }

    public BlockVector west() {
        return west(1);
    }

    public BlockVector west(int step) {
        return getSide(BlockFace.WEST, step);
    }

    public double distance(Vector3d pos) {
        return Math.sqrt(this.distanceSquared(pos));
    }

    public double distance(BlockVector pos) {
        return Math.sqrt(this.distanceSquared(pos));
    }

    public double distanceSquared(Vector3d pos) {
        return distanceSquared(pos.x, pos.y, pos.z);
    }

    public double distanceSquared(BlockVector pos) {
        return distanceSquared(pos.x, pos.y, pos.z);
    }

    public double distanceSquared(double x, double y, double z) {
        return Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2) + Math.pow(this.z - z, 2);
    }

    @Override
    public boolean equals(Object ob) {
        if (ob == null) return false;
        if (ob == this) return true;

        if (!(ob instanceof BlockVector)) return false;

        return this.x == ((BlockVector) ob).x &&
                        this.y == ((BlockVector) ob).y &&
                        this.z == ((BlockVector) ob).z;
    }

    @Override
    public final int hashCode() {
        return (x ^ (z << 12)) ^ (y << 24);
    }

    @Override
    public String toString() {
        return "BlockPosition(level=" + ",x=" + this.x + ",y=" + this.y + ",z=" + this.z + ")";
    }

    @Override
    public BlockVector clone() {
        try {
            return (BlockVector) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}