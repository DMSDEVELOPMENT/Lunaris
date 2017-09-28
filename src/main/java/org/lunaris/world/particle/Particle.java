package org.lunaris.world.particle;

import org.lunaris.Lunaris;
import org.lunaris.entity.Player;
import org.lunaris.network.protocol.packet.Packet19LevelEvent;
import org.lunaris.world.Location;

import java.util.Collection;

/**
 * Created by RINES on 28.09.17.
 */
public abstract class Particle {

    private final int eventID;
    private final ParticleType type;
    private final Location location;
    private final int data;

    Particle(int eventID, ParticleType type, Location location, int data) {
        this.eventID = eventID;
        this.type = type;
        this.location = location;
        this.data = data;
    }

    public ParticleType getType() {
        return this.type;
    }

    public Packet19LevelEvent createPacket() {
        return new Packet19LevelEvent(this.eventID, (float) this.location.getX(), (float) this.location.getY(), (float) this.location.getZ(), this.data);
    }

    public void send(Player player) {
        player.sendPacket(createPacket());
    }

    public void send(Collection<Player> players) {
        Lunaris.getInstance().getNetworkManager().sendPacket(players, createPacket());
    }

    public void sendToNearbyPlayers() {
        send(this.location.getWorld().getApplicablePlayers(this.location));
    }

}