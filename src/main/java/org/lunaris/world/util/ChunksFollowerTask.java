package org.lunaris.world.util;

import org.lunaris.entity.Player;
import org.lunaris.server.IServer;
import org.lunaris.world.Chunk;
import org.lunaris.world.World;

/**
 * Created by RINES on 14.09.17.
 */
public class ChunksFollowerTask {

    private final static int LAUNCH_DELAY = 20;

    private final IServer server;

    private final World world;

    private int ticks;

    public ChunksFollowerTask(IServer server, World world) {
        this.server = server;
        this.world = world;
    }

    public void tick() {
        if(++this.ticks == LAUNCH_DELAY) {
            this.ticks = 0;
            this.server.getLogger().info("Looking around..");
            for(Player p : this.world.getPlayers()) {
                int cx = p.getLocation().getBlockX() >> 4, cz = p.getLocation().getBlockZ() >> 4;
                int r = p.getChunksView();
                this.server.getLogger().info("Looking around %d %d (%d) for %s", cx, cz, r, p.getName());
                for(int x = cx - r; x <= cx + r; ++x)
                    for(int z = cz - r; z <= cz + r; ++z) {
                        Chunk chunk = this.world.loadChunk(x, z);
                        if(!p.hasChunkSent(x, z)) {
                            chunk.sendTo(p);
//                            this.server.getLogger().info("Sent %d %d to %s", x, z, p.getName());
                        }
                    }
            }
        }
    }

}
