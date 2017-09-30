package org.lunaris.network;

import org.lunaris.Lunaris;
import org.lunaris.entity.Player;
import org.lunaris.event.network.PacketSendingAsyncEvent;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.network.protocol.MinePacketProvider;
import org.lunaris.server.Scheduler;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by RINES on 13.09.17.
 */
public class NetworkManager {

    public final static int SUPPORTED_CLIENT_PROTOCOL_VERSION = 137;

    private final Lunaris server;

    private final RakNetProvider rakNet;

    final MinePacketProvider mineProvider;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public NetworkManager(Lunaris server) {
        this.server = server;
        this.rakNet = new RakNetProvider(this, server);
        this.mineProvider = new MinePacketProvider(server, this);
        this.executor.scheduleAtFixedRate(this::tick, 0L, Scheduler.ONE_TICK_IN_MILLIS >> 1, TimeUnit.MILLISECONDS);
    }

    public void disable() {
        this.rakNet.disable();
    }

    public void sendPacket(Player player, MinePacket packet) {
        PacketSendingAsyncEvent event = new PacketSendingAsyncEvent(player, packet);
        this.server.getEventManager().call(event);
        if(event.isCancelled())
            return;
        this.rakNet.sendPacket(player.getPacketsBush(), packet);
    }

    public void sendPacket(Collection<Player> players, MinePacket packet) {
        players.removeIf(p -> {
            PacketSendingAsyncEvent event = new PacketSendingAsyncEvent(p, packet);
            this.server.getEventManager().call(event);
            return event.isCancelled();
        });
        this.rakNet.sendPacket(players.stream().map(Player::getPacketsBush).collect(Collectors.toSet()), packet);
    }

    public void tick() {
        this.server.getPlayerProvider().getAllPlayers().forEach(p -> this.rakNet.tickBush(p.getSession(), p.getPacketsBush()));
    }

}
