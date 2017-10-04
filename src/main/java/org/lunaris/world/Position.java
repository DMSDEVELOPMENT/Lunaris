package org.lunaris.world;

import org.lunaris.block.Block;
import org.lunaris.util.math.Vector3d;

/**
 * Created by RINES on 13.09.17.
 */
public class Position extends Vector3d {

    private final World world;

    public Position(World world, double x, double y, double z) {
        super(x, y, z);
        this.world = world;
    }

    public World getWorld() {
        return this.world;
    }

    public Block getBlock() {
        return this.world.getBlockAt(getBlockX(), getBlockY(), getBlockZ());
    }

    @Override
    public Position add(double x) {
        return this.add(x, 0, 0);
    }

    @Override
    public Position add(double x, double y) {
        return this.add(x, y, 0);
    }

    @Override
    public Position add(double x, double y, double z) {
        return new Position(this.world, this.x + x, this.y + y, this.z + z);
    }

    @Override
    public Position add(Vector3d x) {
        return new Position(this.world, this.x + x.getX(), this.y + x.getY(), this.z + x.getZ());
    }

    @Override
    public Position subtract() {
        return this.subtract(0, 0, 0);
    }

    @Override
    public Position subtract(double x) {
        return this.subtract(x, 0, 0);
    }

    @Override
    public Position subtract(double x, double y) {
        return this.subtract(x, y, 0);
    }

    @Override
    public Position subtract(double x, double y, double z) {
        return this.add(-x, -y, -z);
    }

    @Override
    public Position subtract(Vector3d x) {
        return this.add(-x.getX(), -x.getY(), -x.getZ());
    }

    @Override
    public Position multiply(double number) {
        return new Position(this.world, this.x * number, this.y * number, this.z * number);
    }

    @Override
    public Position divide(double number) {
        return new Position(this.world, this.x / number, this.y / number, this.z / number);
    }

    @Override
    public Position ceil() {
        return new Position(this.world, (int) Math.ceil(this.x), (int) Math.ceil(this.y), (int) Math.ceil(this.z));
    }

    @Override
    public Position floor() {
        return new Position(this.world, this.getBlockX(), this.getBlockY(), this.getBlockZ());
    }

    @Override
    public Position round() {
        return new Position(this.world, Math.round(this.x), Math.round(this.y), Math.round(this.z));
    }

    @Override
    public Position abs() {
        return new Position(this.world, (int) Math.abs(this.x), (int) Math.abs(this.y), (int) Math.abs(this.z));
    }

    @Override
    public Position clone() {
        return (Position) super.clone();
    }

}
