package org.lunaris.world.particle;

import org.lunaris.block.LBlock;
import org.lunaris.network.packet.Packet15UpdateBlock;
import org.lunaris.network.packet.Packet19LevelEvent;

/**
 * Created by RINES on 28.09.17.
 */
public class DestroyBlockParticle extends Particle {

    public DestroyBlockParticle(LBlock block) {
        super(Packet19LevelEvent.EVENT_PARTICLE_DESTROY, null, block.getLocation(), Packet15UpdateBlock.getBlockRuntimeID(block.getTypeId(), block.getData()));
    }

}
