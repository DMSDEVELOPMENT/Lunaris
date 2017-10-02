package org.lunaris.block;

/**
 * @author xtrafrancyz
 */
public enum BUFlag {
    UPDATE_NEIGHBORS,
    SEND_PACKET;

    public static Set set(BUFlag... flags) {
        Set set = new Set();
        set.addAll(flags);
        return set;
    }

    public static Set set(BUFlag flag) {
        Set set = new Set();
        set.add(flag);
        return set;
    }

    public static class Set {
        private boolean[] map;

        private Set() {
            reset();
        }

        public void addAll(BUFlag[] flags) {
            for (BUFlag flag : flags)
                map[flag.ordinal()] = true;
        }

        public void add(BUFlag flag) {
            map[flag.ordinal()] = true;
        }

        public void reset() {
            map = new boolean[BUFlag.values().length];
        }

        public boolean has(BUFlag flag) {
            return map[flag.ordinal()];
        }
    }
}
