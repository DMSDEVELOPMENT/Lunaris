package org.lunaris.util;

/**
 * Created by RINES on 13.09.17.
 */
public class Validate {

    public static void notNull(Object o, String message) {
        if (o == null)
            throw new IllegalArgumentException(message);
    }

    public static void notEmpty(String s, String message) {
        notNull(s, message);
        if (s.isEmpty())
            throw new IllegalArgumentException(message);
    }

}
