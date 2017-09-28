package org.lunaris.world.particle;

import org.lunaris.network.protocol.packet.Packet19LevelEvent;
import org.lunaris.world.Location;

/**
 * Created by RINES on 28.09.17.
 */
public class GenericParticle extends Particle {

    GenericParticle(ParticleType type, Location location) {
        this(type, location, 0);
    }

    GenericParticle(ParticleType type, Location location, int data) {
        super(Packet19LevelEvent.EVENT_ADD_PARTICLE_MASK | type.ordinal(), type, location, data);
    }

}
