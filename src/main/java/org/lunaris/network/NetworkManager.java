package org.lunaris.network;

import org.lunaris.Lunaris;
import org.lunaris.entity.Player;
import org.lunaris.event.network.PacketSendingAsyncEvent;
import org.lunaris.network.protocol.MinePacket;
import org.lunaris.network.protocol.MinePacketProvider;
import org.lunaris.server.IServer;

/**
 * Created by RINES on 13.09.17.
 */
public class NetworkManager {

    public final static int SUPPORTED_CLIENT_PROTOCOL_VERSION = 136;

    private final IServer server;

    private final RakNetProvider rakNet;

    final MinePacketProvider mineProvider;

    public NetworkManager(IServer server) {
        this.server = server;
        this.rakNet = new RakNetProvider(this, (Lunaris) server);
        this.mineProvider = new MinePacketProvider(server);
    }

    public void disable() {
        this.rakNet.disable();
    }

    public void sendPacket(Player player, MinePacket packet) {
        PacketSendingAsyncEvent event = new PacketSendingAsyncEvent(player, packet);
        this.server.getEventManager().call(event);
        if(event.isCancelled())
            return;
        this.rakNet.sendPacket(player.getSession(), packet);
    }

}
