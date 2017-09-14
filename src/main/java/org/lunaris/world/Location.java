package org.lunaris.world;

import org.lunaris.util.math.Vector3d;

/**
 * Created by RINES on 13.09.17.
 */
public class Location extends Position {

    private double yaw, pitch;

    public Location(World world, double x, double y, double z) {
        this(world, x, y, z, 0D, 0D);
    }

    public Location(World world, double x, double y, double z, double yaw, double pitch) {
        super(world, x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    @Override
    public Location add(double x) {
        return this.add(x, 0, 0);
    }

    @Override
    public Location add(double x, double y) {
        return this.add(x, y, 0);
    }

    @Override
    public Location add(double x, double y, double z) {
        return new Location(getWorld(), this.x + x, this.y + y, this.z + z, this.yaw, this.pitch);
    }

    @Override
    public Location add(Vector3d x) {
        return new Location(getWorld(), this.x + x.getX(), this.y + x.getY(), this.z + x.getZ(), this.yaw, this.pitch);
    }

    @Override
    public Location subtract() {
        return this.subtract(0, 0, 0);
    }

    @Override
    public Location subtract(double x) {
        return this.subtract(x, 0, 0);
    }

    @Override
    public Location subtract(double x, double y) {
        return this.subtract(x, y, 0);
    }

    @Override
    public Location subtract(double x, double y, double z) {
        return this.add(-x, -y, -z);
    }

    @Override
    public Location subtract(Vector3d x) {
        return this.add(-x.getX(), -x.getY(), -x.getZ());
    }

    @Override
    public Location multiply(double number) {
        return new Location(getWorld(), this.x * number, this.y * number, this.z * number, this.yaw, this.pitch);
    }

    @Override
    public Location divide(double number) {
        return new Location(getWorld(), this.x / number, this.y / number, this.z / number, this.yaw, this.pitch);
    }

    @Override
    public Location ceil() {
        return new Location(getWorld(), (int) Math.ceil(this.x), (int) Math.ceil(this.y), (int) Math.ceil(this.z), this.yaw, this.pitch);
    }

    @Override
    public Location floor() {
        return new Location(getWorld(), this.getBlockX(), this.getBlockY(), this.getBlockZ(), this.yaw, this.pitch);
    }

    @Override
    public Location round() {
        return new Location(getWorld(), Math.round(this.x), Math.round(this.y), Math.round(this.z), this.yaw, this.pitch);
    }

    @Override
    public Location abs() {
        return new Location(getWorld(), (int) Math.abs(this.x), (int) Math.abs(this.y), (int) Math.abs(this.z), this.yaw, this.pitch);
    }

    public Vector3d getDirectionVector() {
        double pitch = ((getPitch() + 90) * Math.PI) / 180;
        double yaw = ((getYaw() + 90) * Math.PI) / 180;
        double x = Math.sin(pitch) * Math.cos(yaw);
        double z = Math.sin(pitch) * Math.sin(yaw);
        double y = Math.cos(pitch);
        return new Vector3d(x, y, z).normalize();
    }

    @Override
    public Location clone() {
        return (Location) super.clone();
    }

}
