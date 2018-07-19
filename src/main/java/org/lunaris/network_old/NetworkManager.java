package org.lunaris.network_old;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;

import org.lunaris.LunarisServer;
import org.lunaris.entity.LPlayer;
import org.lunaris.event.network.PacketSendingAsyncEvent;
import org.lunaris.network_old.protocol.MineBuffer;
import org.lunaris.network_old.protocol.MinePacket;
import org.lunaris.network_old.protocol.MinePacketProvider;
import org.lunaris.network_old.raknet.RakNetPacket;
import org.lunaris.network_old.raknet.protocol.Reliability;
import org.lunaris.network_old.raknet.session.RakNetClientSession;
import org.lunaris.api.server.Scheduler;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by RINES on 13.09.17.
 */
public class NetworkManager {

    public final static int SUPPORTED_CLIENT_PROTOCOL_VERSION = 274;
    public final static long NETWORK_TICK = Scheduler.ONE_TICK_IN_MILLIS >> 1;

    private final LunarisServer server;

    private final RakNetProvider rakNet;

    final MinePacketProvider mineProvider;

    private final ScheduledExecutorService executor = Scheduler.createScheduledExecutor("Packet Compiler", 1);
    private final Queue<QueuedPacket> sendQueue = new ConcurrentLinkedQueue<>();

    public NetworkManager(LunarisServer server) {
        this.server = server;
        this.rakNet = new RakNetProvider(this, server);
        this.mineProvider = new MinePacketProvider(server, this);
        this.executor.scheduleAtFixedRate(this::tick, 0L, NETWORK_TICK, TimeUnit.MILLISECONDS);
    }

    public void disable() {
        this.rakNet.disable();
    }

    public void sendPacket(LPlayer player, MinePacket packet) {
        PacketSendingAsyncEvent event = new PacketSendingAsyncEvent(player, packet);
        this.server.getEventManager().call(event);
        if (event.isCancelled())
            return;
        sendQueue.add(new QueuedPacket(Collections.singletonList(player.getSession()), serialize(packet)));
    }

    public void sendPacket(Collection<LPlayer> players, MinePacket packet) {
        players.removeIf(p -> {
            PacketSendingAsyncEvent event = new PacketSendingAsyncEvent(p, packet);
            this.server.getEventManager().call(event);
            return event.isCancelled();
        });
        sendQueue.add(new QueuedPacket(players.stream().map(LPlayer::getSession).collect(Collectors.toList()), serialize(packet)));
    }

    public void broadcastPacket(MinePacket packet) {
        sendPacket(new HashSet<>(this.server.getOnlinePlayers()), packet);
    }

    public void tick() {
        Timings.getPacketsSendingTimer().startTiming();
        Set<RakNetClientSession> receivers = new HashSet<>();
        for (QueuedPacket packet = sendQueue.poll(); packet != null; packet = sendQueue.poll()) {
            for (RakNetClientSession session : packet.receivers) {
                session.getPacketsBush().collect(packet.packet);
                if (session.getPacketsBush().isFull())
                    session.sendMessage(Reliability.RELIABLE_ORDERED, new RakNetPacket(session.getPacketsBush().blossom()));
            }
            receivers.addAll(packet.receivers);
        }
        receivers.forEach(session ->
            session.sendMessage(Reliability.RELIABLE_ORDERED, new RakNetPacket(session.getPacketsBush().blossom()))
        );
        Timings.getPacketsSendingTimer().stopTiming();
    }

    private byte[] serialize(MinePacket packet) {
        Timing timing = Timings.getPacketsSerializationTimer(packet);
        timing.startTiming();
        try {
            MineBuffer buffer = new MineBuffer(1 << 4);
            packet.write(buffer);
            byte[] bytes = buffer.readBytes(buffer.readableBytes());
            buffer.release();
            buffer = new MineBuffer(bytes.length + 3 + 4 /*varint*/);
            buffer.writeUnsignedVarInt(bytes.length + 3); //byte id + short (2 bytes)
            buffer.writeByte(packet.getByteId());
            buffer.writeUnsignedShort((short) 0);
            buffer.writeBytes(bytes);
            bytes = buffer.readBytes(buffer.remaining());
            buffer.release();
            return bytes;
        } catch (Exception ex) {
            new Exception("Can not serialize packet", ex).printStackTrace();
            return null;
        } finally {
            timing.stopTiming();
        }
    }

    private static class QueuedPacket {
        public Collection<RakNetClientSession> receivers;
        public byte[] packet;

        public QueuedPacket(Collection<RakNetClientSession> receivers, byte[] packet) {
            this.receivers = receivers;
            this.packet = packet;
        }
    }

}
