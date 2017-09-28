package org.lunaris.server;

import co.aikar.timings.Timings;
import org.lunaris.Lunaris;
import org.lunaris.world.BlockMaster;
import org.lunaris.world.Difficulty;
import org.lunaris.world.Dimension;
import org.lunaris.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by RINES on 14.09.17.
 */
public class WorldProvider {

    private final List<World> worlds = new ArrayList<>();
    private final BlockMaster blockMaster;

    public WorldProvider(Lunaris server) {
        this.worlds.add(new World(server, "Test World", Dimension.OVERWORLD, Difficulty.PEACEFUL));
        this.blockMaster = new BlockMaster(server);
        server.getScheduler().schedule(this::tick, 0L, Scheduler.ONE_TICK_IN_MILLIS, TimeUnit.MILLISECONDS);
    }

    public List<World> getWorlds() {
        return this.worlds;
    }

    public BlockMaster getBlockMaster() {
        return this.blockMaster;
    }

    public World getWorld(int index) {
        return this.worlds.get(index);
    }

    public World getWorld(String name) {
        return this.worlds.stream().filter(w -> w.getName().equals(name)).findAny().orElse(null);
    }

    private void tick() {
//        System.out.println("TICK");
        Timings.worldsTickTimer.startTiming();
        this.worlds.forEach(World::tick);
        Timings.worldsTickTimer.stopTiming();
    }

}
