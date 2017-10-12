package org.lunaris.network;

import co.aikar.timings.Timings;
import io.netty.buffer.Unpooled;
import org.lunaris.Lunaris;
import org.lunaris.entity.LPlayer;
import org.lunaris.event.player.PlayerConnectAsyncEvent;
import org.lunaris.network.protocol.MineBuffer;
import org.lunaris.network.protocol.packet.Packet01Login;
import org.lunaris.network.raknet.RakNet;
import org.lunaris.network.raknet.RakNetLogger;
import org.lunaris.network.raknet.RakNetPacket;
import org.lunaris.network.raknet.identifier.MCPEIdentifier;
import org.lunaris.network.raknet.server.RakNetServer;
import org.lunaris.network.raknet.server.RakNetServerListener;
import org.lunaris.network.raknet.session.RakNetClientSession;
import org.lunaris.network.raknet.stream.PacketDataInput;
import org.lunaris.network.util.ZLib;
import org.lunaris.server.IServer;
import org.lunaris.server.ServerSettings;

import java.util.Random;

/**
 * Created by RINES on 13.09.17.
 */
public class RakNetProvider {

    private final RakNetServer rakNet;
    private final IServer server;
    private final long guid = new Random().nextLong();

    public RakNetProvider(NetworkManager manager, Lunaris server) {
        this.server = server;
        ServerSettings settings = server.getServerSettings();
        this.rakNet = new RakNetServer(
                settings.getHost(),
                settings.getPort(),
                200,
                new MCPEIdentifier(
                        settings.getServerName(),
                        settings.getSupportedClientProtocol(),
                        settings.getSupportedClientVersion(),
                        server.getOnlinePlayers().size(),
                        settings.getMaxPlayersOnServer(),
                        this.guid,
                        server.getWorldProvider().getWorld(0).getName(),
                        "Survival"
                ) {
                    @Override
                    public int getOnlinePlayerCount() {
                        return server.getOnlinePlayers().size();
                    }
                }
        );
        RakNet.enableLogging(RakNetLogger.LEVEL_INFO);
        this.rakNet.setListener(new RakNetServerListener() {
            @Override
            public void onServerStart() {

            }

            @Override
            public void onServerShutdown() {

            }

            @Override
            public void onClientDisconnect(RakNetClientSession session, String reason) {
                LPlayer player = server.getPlayerProvider().removePlayer(session);
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
                Timings.getPacketsReceptionTimer().startTiming();
                MineBuffer buf = null;
                try {
                    PacketDataInput input = packet.getDataInput();
                    byte[] bytes = new byte[input.remaining()];
                    input.readFully(bytes);
                    if(session.getEncryptor() != null)
                        bytes = session.getEncryptor().decryptInputFromClient(bytes);
                    bytes = ZLib.inflate(bytes, 1 << 26);
                    buf = new MineBuffer(Unpooled.copiedBuffer(bytes));
                    int position;
                    while((position = buf.remaining()) > 0) {
                        int payloadLength = buf.readUnsignedVarInt() + position - buf.remaining();
                        byte packetID = buf.readByte();
                        buf.skipBytes(2);
                        if(packetID == 0x01) {
                            Packet01Login minePacket = (Packet01Login) manager.mineProvider.getPacket(packetID, buf);
                            minePacket.setPlayer(server.getPlayerProvider().createPlayer(minePacket, session));
                            manager.mineProvider.handle(minePacket);
                            continue;
                        }
//                        System.out.println("Received " + String.format("0x%02X", packetID));
                        if(!manager.mineProvider.handle(packetID, buf, server.getPlayerProvider().getPlayer(session)))
                            return;
                        int delta = position - buf.remaining();
                        if(delta > payloadLength) {
                            writeDump(bytes);
                            throw new IllegalStateException(String.format("Illegal packet data in 0x%02X: took %d bytes whilst expected %d. Critical.", packetID, delta, payloadLength));
                        }
                        if(delta != payloadLength) {
                            writeDump(bytes);
                            new IllegalStateException(String.format("Illegal packet data in 0x%02X: took %d bytes whilst expected %d.", packetID, delta, payloadLength)).printStackTrace();
                        }
                        buf.skipBytes(payloadLength - delta);
                    }
                }catch(Exception ex) {
                    new Exception("Can not handle packet input data", ex).printStackTrace();
                }finally {
                    if(buf != null)
                        buf.release();
                    Timings.getPacketsReceptionTimer().stopTiming();
                }
            }
        });
        this.rakNet.startThreaded();
    }

    public void disable() {
        this.rakNet.shutdown();
    }

    private void writeDump(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes)
            sb.append("0x").append(Integer.toHexString(b)).append(" ");
        this.server.getLogger().info("PACKET DUMP :: " + sb.toString().trim());
    }

}
