package org.lunaris.network;

import io.gomint.jraknet.Connection;
import io.gomint.jraknet.EncapsulatedPacket;
import io.gomint.jraknet.PacketBuffer;
import io.gomint.jraknet.PacketReliability;
import org.lunaris.LunarisServer;
import org.lunaris.api.event.player.PlayerDisconnectEvent;
import org.lunaris.api.server.Scheduler;
import org.lunaris.entity.LPlayer;
import org.lunaris.event.network.PacketReceivedAsyncEvent;
import org.lunaris.jwt.EncryptionHandler;
import org.lunaris.network.executor.PostProcessExecutor;
import org.lunaris.network.packet.Packet05Disconnect;
import org.lunaris.network.packet.PacketFEBatch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.InflaterInputStream;

/**
 * Created by k.shandurenko on 19.07.2018
 */
public class PlayerConnection {

    private final static byte PACKET_BATCH = -2;

    private final NetworkManager networkManager;
    private final Connection connection;
    private PlayerConnectionState connectionState;
    private PostProcessExecutor postProcessExecutor;
    private BlockingQueue<Packet> sendingQueue = new LinkedBlockingQueue<>();

    private PacketHandler packetHandler;
    private EncryptionHandler encryptionHandler;

    private int protocolVersion;
    private long lastUpdateDt;

    private WeakReference<LPlayer> playerWeakReference;

    PlayerConnection(NetworkManager networkManager, Connection connection, PlayerConnectionState connectionState) {
        this.networkManager = networkManager;
        this.connection = connection;
        this.connectionState = connectionState;
        this.postProcessExecutor = this.networkManager.getPostProcessExecutorService().getExecutor();
        this.connection.addDataProcessor(packetData -> {
            PacketBuffer buffer = new PacketBuffer(packetData.getPacketData(), 0);
            if (buffer.getRemaining() <= 0) {
                return packetData;
            }
            byte packetID = buffer.readByte();
            if (packetID == PACKET_BATCH) {
                byte[] pureData = readBatchPacket(buffer);
                EncapsulatedPacket newPacket = new EncapsulatedPacket();
                newPacket.setPacketData(pureData);
                return newPacket;
            }
            return packetData;
        });
    }

    void close() {
        LPlayer player = getPlayer();
        if (player != null) {
            new PlayerDisconnectEvent(player).call();
            LunarisServer.getInstance().getPlayerProvider().removePlayer(player);
        }
        if (this.postProcessExecutor != null) {
            this.networkManager.getPostProcessExecutorService().releaseExecutor(this.postProcessExecutor);
        }
    }

    public long getGuid() {
        return this.connection.getGuid();
    }

    public void setPacketHandler(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public EncryptionHandler getEncryptionHandler() {
        return this.encryptionHandler;
    }

    public PlayerConnectionState getConnectionState() {
        return this.connectionState;
    }

    public void setConnectionState(PlayerConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    public void setEncryptionHandler(EncryptionHandler encryptionHandler) {
        this.encryptionHandler = encryptionHandler;
    }

    public void setPlayer(LPlayer player) {
        this.playerWeakReference = new WeakReference<>(player);
    }

    public LPlayer getPlayer() {
        return this.playerWeakReference == null ? null : this.playerWeakReference.get();
    }

    public void disconnect(String reason) {
        if (reason != null && !reason.isEmpty()) {
            Packet05Disconnect packet = new Packet05Disconnect(reason);
            sendPacketImmediately(packet);
            LunarisServer.getInstance().getScheduler().runAsync(() -> {
                for (int i = 0; i < 60; ++i) {
                    try {
                        Thread.sleep(50L);
                    } catch (InterruptedException ex) {}
                    if (!this.connection.isConnected()) {
                        return;
                    }
                }
                internalClose(reason);
            });
        } else {
            internalClose(reason);
        }
    }

    void sendPacket(Packet packet) {
        this.sendingQueue.offer(packet);
    }

    public void sendPacketImmediately(Packet packet) {
        if (packet.getID() != PACKET_BATCH) {
            this.postProcessExecutor.addWork(this, packet);
        } else {
            PacketBuffer buffer = new PacketBuffer(64);
            buffer.writeByte(packet.getID());
            packet.write(buffer);
            this.connection.send(PacketReliability.RELIABLE_ORDERED, 0, buffer.getBuffer(), 0, buffer.getPosition());
        }
    }

    private void releaseSendingQueue() {
        if (this.sendingQueue.isEmpty()) {
            return;
        }
        List<Packet> drainedQueue = new ArrayList<>(this.sendingQueue.size());
        this.sendingQueue.drainTo(drainedQueue);
        this.postProcessExecutor.addWork(this, drainedQueue);
    }

    void tick(long currentMillis, long dT) {
        List<PacketBuffer> buffers = null;
        EncapsulatedPacket packetData;
        while ((packetData = this.connection.receive()) != null) {
            if (buffers == null) {
                buffers = new ArrayList<>();
            }
            buffers.add(new PacketBuffer(packetData.getPacketData(), 0));
        }
        if (buffers != null) {
            buffers.forEach(buffer -> prepareAndHandlePacket(currentMillis, buffer));
        }
        this.lastUpdateDt += dT;
        if (this.lastUpdateDt >= Scheduler.ONE_TICK_IN_MILLIS) {
            releaseSendingQueue();
            this.lastUpdateDt = 0L;
        }
    }

    private void prepareAndHandlePacket(long currentMillis, PacketBuffer buffer) {
        if (buffer.getRemaining() <= 0) {
            return;
        }
        while (buffer.getRemaining() > 0) {
            int packetLength = buffer.readUnsignedVarInt();
            byte[] data = new byte[packetLength];
            buffer.readBytes(data);
            PacketBuffer emptyBuffer = new PacketBuffer(data, 0);
            handlePacket(currentMillis, emptyBuffer);
            if (emptyBuffer.getRemaining() > 0) {
                LunarisServer.getInstance().getLogger().warn("Could not read packet 0x%s: remaining %d bytes", Integer.toHexString(data[0]), emptyBuffer.getRemaining());
            }
        }
    }

    private void handlePacket(long currentMillis, PacketBuffer buffer) {
        byte packetID = buffer.readByte();
        if (packetID == PACKET_BATCH) {
            LunarisServer.getInstance().getLogger().warn("Malformed batch packet");
        }
        buffer.readShort(); //???
        Packet packet = this.networkManager.getPacketRegistry().constructPacket(packetID);
        packet.read(buffer);
        packet.setConnection(this);
        PacketReceivedAsyncEvent event = new PacketReceivedAsyncEvent(packet);
        LunarisServer.getInstance().getEventManager().call(event);
        if (event.isCancelled()) {
            return;
        }
        this.packetHandler.handle(packet, currentMillis);
    }

    private void internalClose(String reason) {
        if (this.connection.isConnected() && !this.connection.isDisconnecting()) {
            this.connection.disconnect(reason);
        }
    }

    private byte[] readBatchPacket(PacketBuffer buffer) {
        byte[] input = new byte[buffer.getRemaining()];
        System.arraycopy(buffer.getBuffer(), buffer.getPosition(), input, 0, input.length);
        if (this.encryptionHandler != null) {
            input = this.encryptionHandler.decryptInputFromClient(input);
            if (input == null) {
                disconnect("Wrong checksum of encrypted packet");
                return null;
            }
        }
        InflaterInputStream inflater = new InflaterInputStream(new ByteArrayInputStream(input));
        ByteArrayOutputStream bout = new ByteArrayOutputStream(buffer.getRemaining());
        byte[] slice = new byte[256];
        try {
            int read;
            while ((read = inflater.read(slice)) > -1) {
                bout.write(slice, 0, read);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return bout.toByteArray();
    }

}
