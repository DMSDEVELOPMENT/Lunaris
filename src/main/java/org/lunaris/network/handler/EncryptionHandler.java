package org.lunaris.network.handler;

import org.lunaris.entity.LPlayer;
import org.lunaris.network.PacketHandler;
import org.lunaris.network.PlayerConnectionState;
import org.lunaris.network.packet.Packet02PlayStatus;
import org.lunaris.network.packet.Packet04EncryptionResponse;
import org.lunaris.network.packet.Packet06ResourcePacksInfo;

/**
 * Created by k.shandurenko on 20.07.2018
 */
public class EncryptionHandler extends PacketHandler {

    @Override
    protected void registerPacketHandlers() {
        addHandler(Packet04EncryptionResponse.class, this::handle);
    }

    private void handle(Packet04EncryptionResponse packet, long time) {
        LPlayer player = packet.getConnection().getPlayer();
        player.getConnection().setConnectionState(PlayerConnectionState.LOGIN);
        //setup handler
        player.sendPacket(new Packet02PlayStatus(Packet02PlayStatus.Status.LOGIN_SUCCESS));
        player.sendPacket(new Packet06ResourcePacksInfo());
    }

}
