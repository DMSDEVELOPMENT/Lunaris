package org.lunaris.entity.data;

import org.lunaris.Lunaris;
import org.lunaris.block.Block;
import org.lunaris.entity.Player;
import org.lunaris.server.Scheduler;

import java.util.concurrent.TimeUnit;

/**
 * Created by RINES on 05.10.17.
 */
public class BlockBreakingData {

    private Scheduler.Task blockBreakingTask;
    private long blockBreakingTime;

    public void clear() {
        if(this.blockBreakingTask != null) {
            this.blockBreakingTask.cancel();
            this.blockBreakingTask = null;
        }
        this.blockBreakingTime = 0L;
    }

    public void runBreak(Player player, Block block, long time) {
        if(isBreakingBlock())
            clear();
        this.blockBreakingTime = time;
        this.blockBreakingTask = Lunaris.getInstance().getScheduler().schedule(
                () -> Lunaris.getInstance().getWorldProvider().getBlockMaster().processBlockBreak(player, block),
                time - Scheduler.ONE_TICK_IN_MILLIS,
                TimeUnit.MILLISECONDS
        );
    }

    public boolean isBreakingBlock() {
        return this.blockBreakingTask != null;
    }

    public long getBlockBreakingTime() {
        return this.blockBreakingTime;
    }

}
