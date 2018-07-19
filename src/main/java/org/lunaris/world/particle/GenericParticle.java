package org.lunaris.world.particle;

import org.lunaris.network_old.protocol.packet.Packet19LevelEvent;
import org.lunaris.api.world.Location;

/**
 * Created by RINES on 28.09.17.
 */
public class GenericParticle extends Particle {

    public GenericParticle(ParticleType type, Location location) {
        this(type, location, 0);
    }

    public GenericParticle(ParticleType type, Location location, int data) {
        super(Packet19LevelEvent.EVENT_ADD_PARTICLE_MASK | type.ordinal(), type, location, data);
    }

}
