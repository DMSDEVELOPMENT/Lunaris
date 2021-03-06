package org.lunaris.server;

import org.lunaris.LunarisServer;
import org.lunaris.api.server.Scheduler;
import org.lunaris.api.world.Difficulty;
import org.lunaris.api.world.Dimension;
import org.lunaris.world.BlockMaster;
import org.lunaris.world.LWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by RINES on 14.09.17.
 */
public class WorldProvider {

    private final static long TARGET_TPS_NANOS = TimeUnit.SECONDS.toNanos(1) / 20;

    private final List<LWorld> worlds = new ArrayList<>();
    private final BlockMaster blockMaster;

    private float lastTickTime;

    public WorldProvider(LunarisServer server) {
        this.worlds.add(new LWorld(server, "Test World", Dimension.OVERWORLD, Difficulty.PEACEFUL));
        this.blockMaster = new BlockMaster(server);
        server.getScheduler().scheduleRepeatable(this::tick, 0L, Scheduler.ONE_TICK_IN_MILLIS, TimeUnit.MILLISECONDS);
    }

    public List<LWorld> getWorlds() {
        return this.worlds;
    }

    public BlockMaster getBlockMaster() {
        return this.blockMaster;
    }

    public LWorld getWorld(int index) {
        return index >= this.worlds.size() ? null : this.worlds.get(index);
    }

    public LWorld getWorld(String name) {
        return this.worlds.stream().filter(w -> w.getName().equals(name)).findAny().orElse(null);
    }

    public float getLastTickTime() {
        return this.lastTickTime;
    }

    private void tick() {
        long currentNanos = System.nanoTime();
        long current = System.currentTimeMillis();
        this.worlds.forEach(world -> world.tick(current, this.lastTickTime));
        long diff = System.nanoTime() - currentNanos;
        this.lastTickTime = (diff < TARGET_TPS_NANOS ? TARGET_TPS_NANOS : diff) / 1_000_000_000F;
    }

}
