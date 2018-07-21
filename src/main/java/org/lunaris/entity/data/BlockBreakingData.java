package org.lunaris.entity.data;

/**
 * Created by RINES on 05.10.17.
 */
public class BlockBreakingData {

    private long blockBreakingTime, breakStartTime, passed;

    public void clear() {
        this.blockBreakingTime = 0L;
        this.breakStartTime = 0L;
        this.passed = 0L;
    }

    public void startBreak(long time) {
        this.blockBreakingTime = time;
        this.breakStartTime = System.currentTimeMillis();
        this.passed = 0L;
    }

    public void updateBreak(long newTime) {
        long current = System.currentTimeMillis();
        this.passed += current - this.breakStartTime;
        this.breakStartTime = current;
        this.blockBreakingTime = newTime - this.passed;
    }

    public boolean isBreakingBlock() {
        return this.blockBreakingTime != 0L;
    }

    public long getBlockBreakingTime() {
        return this.blockBreakingTime;
    }

    public long getBreakStartTime() {
        return this.breakStartTime;
    }

    public long getOvertime() {
        return this.passed;
    }

}
