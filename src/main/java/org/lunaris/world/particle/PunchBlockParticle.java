package org.lunaris.world.particle;

import org.lunaris.api.world.BlockFace;
import org.lunaris.block.LBlock;
import org.lunaris.network.packet.Packet15UpdateBlock;
import org.lunaris.network.packet.Packet19LevelEvent;

/**
 * Created by RINES on 28.09.17.
 */
public class PunchBlockParticle extends Particle {

    public PunchBlockParticle(LBlock block, BlockFace face) {
        super(Packet19LevelEvent.EVENT_PARTICLE_PUNCH_BLOCK, null, block.getLocation(), Packet15UpdateBlock.getBlockRuntimeID(block.getTypeId(), block.getData()) | (face.getIndex() << 24));
    }

}
