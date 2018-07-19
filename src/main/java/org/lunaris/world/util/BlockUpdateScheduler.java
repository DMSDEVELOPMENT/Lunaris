package org.lunaris.world.util;

import org.lunaris.api.material.Material;
import org.lunaris.block.LBlock;
import org.lunaris.world.BlockUpdateType;
import org.lunaris.world.BlockVector;
import org.lunaris.world.LWorld;

import java.util.*;

/**
 * @author xtrafrancyz
 */
public class BlockUpdateScheduler {
    private final LWorld world;
    private final Set<ScheduledUpdate> updates = new HashSet<>();
    private final List<ScheduledUpdate> updatesThisTick = new LinkedList<>();

    public BlockUpdateScheduler(LWorld world) {
        this.world = world;
    }

    public void tick() {
        Iterator<ScheduledUpdate> it = updates.iterator();
        while (it.hasNext()) {
            ScheduledUpdate update = it.next();
            if (!world.isChunkLoadedAt(update.pos.x >> 4, update.pos.z >> 4)) {
                it.remove();
                continue;
            }
            if (update.delay-- == 0) {
                it.remove();
                updatesThisTick.add(update);
            }
        }
        for (ScheduledUpdate update : updatesThisTick) {
            LBlock block = world.getBlockAt(update.pos.x, update.pos.y, update.pos.z);
            if (block.getType() != update.material)
                continue;
            block.getHandle().onUpdate(block, BlockUpdateType.SCHEDULED);
        }
        updatesThisTick.clear();
    }

    public void scheduleUpdate(LBlock block, int delayTicks) {
        updates.add(new ScheduledUpdate(block, delayTicks));
    }

    private static class ScheduledUpdate {
        public final BlockVector pos;
        public final Material material;
        public int delay;

        public ScheduledUpdate(LBlock block, int delay) {
            this.pos = new BlockVector(block.getX(), block.getY(), block.getZ());
            this.material = block.getType();
            this.delay = delay;
        }

        @Override
        public int hashCode() {
            return pos.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            ScheduledUpdate o = (ScheduledUpdate) obj;
            return o.pos.equals(pos) && o.material == material;
        }
    }
}
