package org.lunaris.util.math;

/**
 * Created by RINES on 12.09.17.
 */
public class LMath {

    private static final int BIG_ENOUGH_INT = 16 * 1024;
    private static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
    private static final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5;
    public final static float EPSILON = 1E-5F;

    public static float pow2(float value) {
        return value * value;
    }

    public static double pow2(double value) {
        return value * value;
    }

    public static int floorDouble(double n) {
        int i = (int) n;
        return n >= i ? i : i - 1;
    }

    public static int ceilDouble(double n) {
        int i = (int) (n + 1);
        return n >= i ? i : i - 1;
    }

    public static int floorFloat(float n) {
        int i = (int) n;
        return n >= i ? i : i - 1;
    }

    public static int fastFloor(float x) {
        return (int) (x + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }

    public static int fastCeil(float x) {
        return BIG_ENOUGH_INT - (int) (BIG_ENOUGH_FLOOR - x);
    }

    public static int ceilFloat(float n) {
        int i = (int) (n + 1);
        return n >= i ? i : i - 1;
    }

    public static int randomRange(LRandom random) {
        return randomRange(random, 0);
    }

    public static int randomRange(LRandom random, int start) {
        return randomRange(random, 0, 0x7fffffff);
    }

    public static int randomRange(LRandom random, int start, int end) {
        return start + (random.nextInt() % (end + 1 - start));
    }

    public static double round(double d) {
        return round(d, 0);
    }

    public static double round(double d, int precision) {
        return ((double) Math.round(d * Math.pow(10, precision))) / Math.pow(10, precision);
    }

    public static double clamp(double check, double min, double max) {
        return check > max ? max : (check < min ? min : check);
    }

    public static double getDirection(double d0, double d1) {
        if (d0 < 0.0D) {
            d0 = -d0;
        }

        if (d1 < 0.0D) {
            d1 = -d1;
        }

        return d0 > d1 ? d0 : d1;
    }

}
