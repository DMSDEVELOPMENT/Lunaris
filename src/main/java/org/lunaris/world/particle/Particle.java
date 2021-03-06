package org.lunaris.world.particle;

import org.lunaris.LunarisServer;
import org.lunaris.api.world.Location;
import org.lunaris.entity.LPlayer;
import org.lunaris.network.packet.Packet19LevelEvent;

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

    public void send(LPlayer player) {
        player.sendPacket(createPacket());
    }

    public void send(Collection<LPlayer> players) {
        LunarisServer.getInstance().getNetworkManager().sendPacket(players, createPacket());
    }

    public void sendToNearbyPlayers() {
        send((Collection<LPlayer>) this.location.getWorld().getWatcherPlayers(this.location));
    }

    public void sendImmediately(LPlayer player) {
        player.sendPacket(createPacket());
    }

    public void sendImmediately(Collection<LPlayer> players) {
        LunarisServer.getInstance().getNetworkManager().sendPacketImmediately(players, createPacket());
    }

    public void sendToNearbyPlayersImmediately() {
        sendImmediately((Collection<LPlayer>) this.location.getWorld().getWatcherPlayers(this.location));
    }

}
