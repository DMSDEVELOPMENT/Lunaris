package org.lunaris.world.particle;

import org.lunaris.block.LBlock;
import org.lunaris.api.world.BlockFace;
import org.lunaris.network.protocol.packet.Packet19LevelEvent;

/**
 * Created by RINES on 28.09.17.
 */
public class PunchBlockParticle extends Particle {

    public PunchBlockParticle(LBlock block, BlockFace face) {
        super(Packet19LevelEvent.EVENT_PARTICLE_PUNCH_BLOCK, null, block.getLocation(), block.getTypeId() | (block.getData() << 8) | (face.getIndex() << 16));
    }

}
