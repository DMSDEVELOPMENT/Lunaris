package org.lunaris.world.util;

/**
 * Created by RINES on 13.09.17.
 */
public class LongHash {

    public static long toLong(int msw, int lsw) {
        return ((long) msw << 32) + lsw - Integer.MIN_VALUE;
    }

    public static int msw(long l) {
        return (int) (l >> 32);
    }

    public static int lsw(long l) {
        return (int) (l & 0xFFFFFFFF) + Integer.MIN_VALUE;
    }

    public static int toHash(int x, int y, int z) {
        x ^= x >>> 32;
        y ^= y >>> 32;
        z ^= z >>> 32;
        return x + 31 * (y + 31 * z);
    }

}