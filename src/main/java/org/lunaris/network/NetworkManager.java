package org.lunaris.network;

import io.gomint.jraknet.EventLoops;
import io.gomint.jraknet.ServerSocket;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.lunaris.LunarisServer;
import org.lunaris.api.event.network.PingAsyncEvent;
import org.lunaris.api.event.player.PlayerConnectAsyncEvent;
import org.lunaris.entity.LPlayer;
import org.lunaris.event.network.PacketSendingAsyncEvent;
import org.lunaris.network.executor.PostProcessExecutorService;
import org.lunaris.network.handler.HandshakeHandler;
import org.lunaris.server.ServerSettings;

import java.net.SocketException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.LongConsumer;

/**
 * Created by k.shandurenko on 19.07.2018
 */
public class NetworkManager {

    private final LunarisServer server;
    private final ServerSocket serverSocket;

    private final LongSet closedConnections = new LongOpenHashSet();
    private final Queue<PlayerConnection> incomingConnections = new ConcurrentLinkedQueue<>();
    private final Long2ObjectMap<PlayerConnection> playersByGuid = new Long2ObjectOpenHashMap<>();
    private final PostProcessExecutorService postProcessExecutorService = new PostProcessExecutorService();

    private final PacketRegistry packetRegistry = new PacketRegistry();
    private final PacketHandler handshakeHandler = new HandshakeHandler();

    public NetworkManager(LunarisServer server) {
        System.setProperty( "java.net.preferIPv4Stack", "true" );               // We currently don't use ipv6
        System.setProperty( "io.netty.selectorAutoRebuildThreshold", "0" );     // Never rebuild selectors
        this.server = server;
        this.serverSocket = new ServerSocket(200);
        initServerSocket();
    }

    public void shutdown() {
        if (this.serverSocket == null) {
            return;
        }
        this.serverSocket.close();
        this.playersByGuid.values().forEach(PlayerConnection::close);
        try {
            EventLoops.LOOP_GROUP.shutdownGracefully().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public PostProcessExecutorService getPostProcessExecutorService() {
        return this.postProcessExecutorService;
    }

    public PacketRegistry getPacketRegistry() {
        return this.packetRegistry;
    }

    public void sendPacket(LPlayer player, Packet packet) {
        PacketSendingAsyncEvent event = new PacketSendingAsyncEvent(player, packet);
        this.server.getEventManager().call(event);
        if (event.isCancelled()) {
            return;
        }
        player.getConnection().sendPacket(packet);
    }

    public void sendPacket(Collection<LPlayer> players, Packet packet) {
        players.forEach(player -> {
            PacketSendingAsyncEvent event = new PacketSendingAsyncEvent(player, packet);
            this.server.getEventManager().call(event);
            if (event.isCancelled()) {
                return;
            }
            player.getConnection().sendPacket(packet);
        });
    }

    public void broadcastPacket(Packet packet) {
        sendPacket(new HashSet<>(this.server.getOnlinePlayers()), packet);
    }

    public void tick(long currentMillis, long deltaFromLastTickTime) {
        while (!this.incomingConnections.isEmpty()) {
            PlayerConnection connection = this.incomingConnections.poll();
            this.playersByGuid.put(connection.getGuid(), connection);
        }
        synchronized (this.closedConnections) {
            if (!this.closedConnections.isEmpty()) {
                this.closedConnections.forEach((LongConsumer) guid -> {
                    PlayerConnection connection = this.playersByGuid.remove(guid);
                    if (connection != null) {
                        connection.close();
                    }
                });
                this.closedConnections.clear();
            }
        }
        this.playersByGuid.values().forEach(connection -> connection.tick(currentMillis, deltaFromLastTickTime));
    }

    private void initServerSocket() {
        this.serverSocket.setMojangModificationEnabled(true);
        ServerSettings settings = this.server.getServerSettings();
        this.serverSocket.setEventHandler((socket, socketEvent) -> {
            switch (socketEvent.getType()) {
                case NEW_INCOMING_CONNECTION: {
                    PlayerConnectAsyncEvent event = new PlayerConnectAsyncEvent(socketEvent.getConnection().getAddress());
                    this.server.getEventManager().call(event);
                    if (event.isCancelled()) {
                        socketEvent.getConnection().disconnect(null);
                    }
                    PlayerConnection connection = new PlayerConnection(this, socketEvent.getConnection(), PlayerConnectionState.HANDSHAKE);
                    connection.setPacketHandler(this.handshakeHandler);
                    this.incomingConnections.offer(connection);
                    break;
                }
                case CONNECTION_CLOSED:
                case CONNECTION_DISCONNECTED: {
                    synchronized (this.closedConnections) {
                        this.closedConnections.add(socketEvent.getConnection().getGuid());
                    }
                    break;
                }
                case UNCONNECTED_PING: {
                    PingAsyncEvent event = new PingAsyncEvent(
                            settings.getServerName(),
                            this.server.getOnlinePlayers().size(),
                            settings.getMaxPlayersOnServer()
                    );
                    this.server.getEventManager().call(event);
                    socketEvent.getPingPongInfo().setMotd(
                            "MCPE;" + event.getMotd() + ";" + settings.getSupportedClientProtocol() + ";" +
                                    settings.getSupportedClientVersion() + ";" + event.getAmountOfPlayers() + ";" +
                                    event.getMaxPlayers()
                    );
                    break;
                }
                default:
                    break;
            }
        });
        try {
            this.serverSocket.bind(settings.getHost(), settings.getPort());
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

}
