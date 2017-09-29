package org.lunaris.util.math;

/**
 * Created by RINES on 12.09.17.
 */
public class MathHelper {

    public static int abs(int number) {
        if (number > 0) {
            return number;
        } else {
            return -number;
        }
    }

    public static int pow2(int a) {
        return a * a;
    }
}
