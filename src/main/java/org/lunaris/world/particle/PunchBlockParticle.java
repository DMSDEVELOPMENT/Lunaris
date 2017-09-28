package org.lunaris.world.particle;

import org.lunaris.block.Block;
import org.lunaris.block.BlockFace;
import org.lunaris.network.protocol.packet.Packet19LevelEvent;

/**
 * Created by RINES on 28.09.17.
 */
public class PunchBlockParticle extends Particle {

    public PunchBlockParticle(Block block, BlockFace face) {
        super(Packet19LevelEvent.EVENT_PARTICLE_PUNCH_BLOCK, null, block.getLocation(), block.getId() | (block.getData() << 8) | (face.getIndex() << 16));
    }

}
