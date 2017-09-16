package org.lunaris.network;

import co.aikar.timings.Timings;
import io.netty.buffer.Unpooled;
import org.lunaris.Lunaris;
import org.lunaris.entity.Player;
import org.lunaris.event.player.PlayerConnectAsyncEvent;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.network.protocol.packet.Packet01Login;
import org.lunaris.network.raknet.RakNetPacket;
import org.lunaris.network.raknet.identifier.MCPEIdentifier;
import org.lunaris.network.raknet.protocol.Reliability;
import org.lunaris.network.raknet.server.RakNetServer;
import org.lunaris.network.raknet.server.RakNetServerListener;
import org.lunaris.network.raknet.session.RakNetClientSession;
import org.lunaris.network.raknet.stream.PacketDataInput;
import org.lunaris.network.util.ZLib;
import org.lunaris.server.IServer;
import org.lunaris.server.ServerSettings;

import java.util.Collection;
import java.util.Random;

/**
 * Created by RINES on 13.09.17.
 */
public class RakNetProvider {

    private final RakNetServer rakNet;
    private final IServer server;
    private final long guid = new Random().nextLong();

    private final int compressionLevel;
    private final byte prefixedId;

    public RakNetProvider(NetworkManager manager, Lunaris server) {
        this.server = server;
        this.compressionLevel = server.getServerSettings().getNetworkCompressionLevel();
        this.prefixedId = server.getServerSettings().getNetworkPacketPrefixedId();
        ServerSettings settings = server.getServerSettings();
        this.rakNet = new RakNetServer(
                settings.getHost(),
                settings.getPort(),
                10,
                new MCPEIdentifier(
                        settings.getServerName(),
                        settings.getSupportedClientProtocol(),
                        settings.getSupportedClientVersion(),
                        server.getOnlinePlayers().size(),
                        settings.getMaxPlayersOnServer(),
                        this.guid,
                        server.getWorldProvider().getWorld(0).getName(),
                        "Survival"
                )
        );
        this.rakNet.setListener(new RakNetServerListener() {
            @Override
            public void onServerStart() {

            }

            @Override
            public void onServerShutdown() {

            }

            @Override
            public void onClientDisconnect(RakNetClientSession session, String reason) {
                Player player = server.getPlayerProvider().removePlayer(session);
                if(player != null)
                    player.tryDisconnectReason("Timed out");
            }

            @Override
            public void onClientConnect(RakNetClientSession session) {
                PlayerConnectAsyncEvent event = new PlayerConnectAsyncEvent(session.getAddress());
                server.getEventManager().call(event);
            }

            @Override
            public void handleMessage(RakNetClientSession session, RakNetPacket packet, int channel) {
                try {
                    Timings.packetsReceptionTimer.startTiming();
                    PacketDataInput input = packet.getDataInput();
                    byte[] bytes = new byte[input.remaining()];
                    input.readFully(bytes);
                    bytes = ZLib.inflate(bytes, 1 << 26);
                    MineBuffer buf = new MineBuffer(Unpooled.copiedBuffer(bytes));
                    buf.readVarInt(); //payload length in bytes
                    byte packetID = buf.readByte();
                    buf.skipBytes(2);
//                    if(packetID != 0x01) {
//                        List<Byte> list = new ArrayList<>();
//                        for(byte b : bytes)
//                            list.add(b);
//                        System.out.println(list.stream().map(b -> String.format("0x%02X", b)).collect(Collectors.joining(" ")));
//                    }
                    try {
                        if(packetID == 0x01) {
                            Packet01Login minePacket = (Packet01Login) manager.mineProvider.getPacket(packetID, buf);
                            minePacket.setPlayer(server.getPlayerProvider().createPlayer(minePacket, session));
                            manager.mineProvider.handle(minePacket);
                            return;
                        }
                        manager.mineProvider.handle(packetID, buf, server.getPlayerProvider().getPlayer(session));
                    }finally {
                        buf.release();
                        Timings.packetsReceptionTimer.stopTiming();
                    }
                }catch(Exception ex) {
                    new Exception("Can not handle packet input data", ex).printStackTrace();
                }
            }
        });
        this.rakNet.startThreaded();
    }

    public void disable() {
        this.rakNet.shutdown();
    }

    public void sendPacket(Collection<RakNetClientSession> sessions, MinePacket packet) {
        try {
            Timings.packetsSendingTimer.startTiming();
            MineBuffer packetBuffer = new MineBuffer(1 << 4);
            packet.write(packetBuffer);
            byte[] bytes = packetBuffer.readBytes(packetBuffer.readableBytes());
            packetBuffer.release();
            MineBuffer buffer = new MineBuffer(1 << 5);
            buffer.writeUnsignedVarInt(bytes.length + 3); //byte id + short (2 bytes)
            buffer.writeByte(packet.getByteId());
            buffer.writeUnsignedShort((short) 0);
            buffer.writeBytes(bytes);
            bytes = buffer.readBytes(buffer.remaining());
            buffer.release();
            bytes = ZLib.deflate(bytes, this.compressionLevel);
            byte[] result = new byte[bytes.length + 1];
            result[0] = this.prefixedId;
            System.arraycopy(bytes, 0, result, 1, bytes.length);
            sessions.forEach(s -> s.sendMessage(Reliability.RELIABLE_ORDERED, new RakNetPacket(result))); //check whether rak net packet can be only 1
            Timings.packetsSendingTimer.stopTiming();
        }catch(Exception ex) {
            new Exception("Can not send packet", ex).printStackTrace();
        }
    }

    public void sendPacket(RakNetClientSession session, MinePacket packet) {
        try {
            Timings.packetsSendingTimer.startTiming();
            MineBuffer packetBuffer = new MineBuffer(1 << 4);
            packet.write(packetBuffer);
            byte[] bytes = packetBuffer.readBytes(packetBuffer.readableBytes());
            packetBuffer.release();
            MineBuffer buffer = new MineBuffer(1 << 5);
            buffer.writeUnsignedVarInt(bytes.length + 3); //byte id + short (2 bytes)
            buffer.writeByte(packet.getByteId());
            buffer.writeUnsignedShort((short) 0);
            buffer.writeBytes(bytes);
            bytes = buffer.readBytes(buffer.remaining());
            buffer.release();
            bytes = ZLib.deflate(bytes, this.compressionLevel);
            byte[] result = new byte[bytes.length + 1];
            result[0] = this.prefixedId;
            System.arraycopy(bytes, 0, result, 1, bytes.length);
            session.sendMessage(Reliability.RELIABLE_ORDERED, new RakNetPacket(result));
            Timings.packetsSendingTimer.stopTiming();
        }catch(Exception ex) {
            new Exception("Can not send packet", ex).printStackTrace();
        }
    }

}
