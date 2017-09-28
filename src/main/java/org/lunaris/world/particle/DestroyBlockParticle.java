package org.lunaris.world.particle;

import org.lunaris.block.Block;
import org.lunaris.network.protocol.packet.Packet19LevelEvent;

/**
 * Created by RINES on 28.09.17.
 */
public class DestroyBlockParticle extends Particle {

    public DestroyBlockParticle(Block block) {
        super(Packet19LevelEvent.EVENT_PARTICLE_DESTROY, null, block.getLocation(), block.getId() | (block.getData() << 8));
    }

}
