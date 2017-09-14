package org.lunaris.util.math;

/**
 * Created by RINES on 12.09.17.
 */
public class Vector3d implements Cloneable {

    public static final int SIDE_DOWN = 0;
    public static final int SIDE_UP = 1;
    public static final int SIDE_NORTH = 2;
    public static final int SIDE_SOUTH = 3;
    public static final int SIDE_WEST = 4;
    public static final int SIDE_EAST = 5;

    public double x;
    public double y;
    public double z;

    public Vector3d() {
        this(0, 0, 0);
    }

    public Vector3d(double x) {
        this(x, 0, 0);
    }

    public Vector3d(double x, double y) {
        this(x, y, 0);
    }

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public int getBlockX() {
        return LMath.floorDouble(this.x);
    }

    public int getBlockY() {
        return LMath.floorDouble(this.y);
    }

    public int getBlockZ() {
        return LMath.floorDouble(this.z);
    }

    public double getRight() {
        return this.x;
    }

    public double getUp() {
        return this.y;
    }

    public double getForward() {
        return this.z;
    }

    public double getSouth() {
        return this.x;
    }

    public double getWest() {
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

    public Vector3d subtract() {
        return this.subtract(0, 0, 0);
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

    public Vector3d multiply(double number) {
        return new Vector3d(this.x * number, this.y * number, this.z * number);
    }

    public Vector3d divide(double number) {
        return new Vector3d(this.x / number, this.y / number, this.z / number);
    }

    public Vector3d ceil() {
        return new Vector3d((int) Math.ceil(this.x), (int) Math.ceil(this.y), (int) Math.ceil(this.z));
    }

    public Vector3d floor() {
        return new Vector3d(this.getBlockX(), this.getBlockY(), this.getBlockZ());
    }

    public Vector3d round() {
        return new Vector3d(Math.round(this.x), Math.round(this.y), Math.round(this.z));
    }

    public Vector3d abs() {
        return new Vector3d((int) Math.abs(this.x), (int) Math.abs(this.y), (int) Math.abs(this.z));
    }

    public Vector3d getSide(int side) {
        return this.getSide(side, 1);
    }

    public Vector3d getSide(int side, int step) {
        switch (side) {
            case Vector3d.SIDE_DOWN:
                return new Vector3d(this.x, this.y - step, this.z);
            case Vector3d.SIDE_UP:
                return new Vector3d(this.x, this.y + step, this.z);
            case Vector3d.SIDE_NORTH:
                return new Vector3d(this.x, this.y, this.z - step);
            case Vector3d.SIDE_SOUTH:
                return new Vector3d(this.x, this.y, this.z + step);
            case Vector3d.SIDE_WEST:
                return new Vector3d(this.x - step, this.y, this.z);
            case Vector3d.SIDE_EAST:
                return new Vector3d(this.x + step, this.y, this.z);
            default:
                return this;
        }
    }

    public static int getOppositeSide(int side) {
        switch (side) {
            case Vector3d.SIDE_DOWN:
                return Vector3d.SIDE_UP;
            case Vector3d.SIDE_UP:
                return Vector3d.SIDE_DOWN;
            case Vector3d.SIDE_NORTH:
                return Vector3d.SIDE_SOUTH;
            case Vector3d.SIDE_SOUTH:
                return Vector3d.SIDE_NORTH;
            case Vector3d.SIDE_WEST:
                return Vector3d.SIDE_EAST;
            case Vector3d.SIDE_EAST:
                return Vector3d.SIDE_WEST;
            default:
                return -1;
        }
    }

    public double distance(Vector3d pos) {
        return Math.sqrt(this.distanceSquared(pos));
    }

    public double distanceSquared(Vector3d pos) {
        return Math.pow(this.x - pos.x, 2) + Math.pow(this.y - pos.y, 2) + Math.pow(this.z - pos.z, 2);
    }

    public double maxPlainDistance() {
        return this.maxPlainDistance(0, 0);
    }

    public double maxPlainDistance(double x) {
        return this.maxPlainDistance(x, 0);
    }

    public double maxPlainDistance(double x, double z) {
        return Math.max(Math.abs(this.x - x), Math.abs(this.z - z));
    }

    public double maxPlainDistance(Vector2f vector) {
        return this.maxPlainDistance(vector.x, vector.y);
    }

    public double maxPlainDistance(Vector3d x) {
        return this.maxPlainDistance(x.x, x.z);
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public Vector3d normalize() {
        double len = this.lengthSquared();
        if (len > 0) {
            return this.divide((double) Math.sqrt(len));
        }
        return new Vector3d(0, 0, 0);
    }

    public double dot(Vector3d v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }

    public Vector3d cross(Vector3d v) {
        return new Vector3d(
                this.y * v.z - this.z * v.y,
                this.z * v.x - this.x * v.z,
                this.x * v.y - this.y * v.x
        );
    }

    /**
     * Returns a new vector with x value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vector3d getIntermediateWithXValue(Vector3d v, double x) {
        double xDiff = v.x - this.x;
        double yDiff = v.y - this.y;
        double zDiff = v.z - this.z;
        if (xDiff * xDiff < 0.0000001) {
            return null;
        }
        double f = (x - this.x) / xDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return new Vector3d(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    /**
     * Returns a new vector with y value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vector3d getIntermediateWithYValue(Vector3d v, double y) {
        double xDiff = v.x - this.x;
        double yDiff = v.y - this.y;
        double zDiff = v.z - this.z;
        if (yDiff * yDiff < 0.0000001) {
            return null;
        }
        double f = (y - this.y) / yDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return new Vector3d(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    /**
     * Returns a new vector with z value equal to the second parameter, along the line between this vector and the
     * passed in vector, or null if not possible.
     */
    public Vector3d getIntermediateWithZValue(Vector3d v, double z) {
        double xDiff = v.x - this.x;
        double yDiff = v.y - this.y;
        double zDiff = v.z - this.z;
        if (zDiff * zDiff < 0.0000001) {
            return null;
        }
        double f = (z - this.z) / zDiff;
        if (f < 0 || f > 1) {
            return null;
        } else {
            return new Vector3d(this.x + xDiff * f, this.y + yDiff * f, this.z + zDiff * f);
        }
    }

    public Vector3d setComponents(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override
    public String toString() {
        return "Vector3(x=" + this.x + ",y=" + this.y + ",z=" + this.z + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3d)) {
            return false;
        }

        Vector3d other = (Vector3d) obj;

        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    public int rawHashCode() {
        return super.hashCode();
    }

    @Override
    public Vector3d clone() {
        try {
            return (Vector3d) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

}
