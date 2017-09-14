package org.lunaris.world.util;

import org.lunaris.entity.Player;
import org.lunaris.server.IServer;
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
            for(Player p : this.world.getPlayers()) {
                int cx = p.getLocation().getBlockX() >> 4, cz = p.getLocation().getBlockZ() >> 4;
                int r = this.server.getServerSettings().getChunksView();
                for(int x = cx - r; x <= cx + r; ++x)
                    for(int z = cz - r; z <= cz + r; ++z)
                        this.world.loadChunk(x, z);
            }
        }
    }

}
