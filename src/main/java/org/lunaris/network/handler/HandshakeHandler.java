package org.lunaris.network.handler;

import org.lunaris.LunarisServer;
import org.lunaris.network.PacketHandler;
import org.lunaris.network.PlayerConnection;
import org.lunaris.network.packet.Packet01Login;

/**
 * Created by k.shandurenko on 19.07.2018
 */
public class HandshakeHandler extends PacketHandler {
    @Override
    protected void registerPacketHandlers() {
        addHandler(Packet01Login.class, this::handle);
    }

    private void handle(Packet01Login packet, long time) {
        PlayerConnection connection = packet.getConnection();
        connection.setProtocolVersion(packet.getProtocolVersion());
        int supportedProtocolVersion = LunarisServer.getInstance().getServerSettings().getSupportedClientProtocol();
        if (connection.getProtocolVersion() != supportedProtocolVersion) {
            String supportedVersionName = LunarisServer.getInstance().getServerSettings().getSupportedClientVersion();
            if (connection.getProtocolVersion() < supportedProtocolVersion) {
                connection.disconnect("Your client is outdated\nWe support version " + supportedVersionName);
            } else {
                connection.disconnect("Our server is outdated\nWe support version " + supportedVersionName);
            }
            return;
        }
        if (packet.getDisconnectReason() != null) {
            connection.disconnect(packet.getDisconnectReason());
            return;
        }
        boolean valid = true;
        String name = packet.getUsername();
        if(name.length() < 3 || name.length() > 16)
            valid = false;
        else {
            if(name.contains(" ")) {
                connection.disconnect("We don't allow spaces in names, sorreh");
                return;
            }
            for(char c : name.toLowerCase().toCharArray())
                if(!(c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '-' || c == '_')) {
                    valid = false;
                    break;
                }
        }
        if(!valid) {
            connection.disconnect("Your nickname is invalid");
            return;
        }
        sync(() -> {

        });
    }

}
