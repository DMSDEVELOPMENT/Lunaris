package org.lunaris.network.handler;

import org.lunaris.LunarisServer;
import org.lunaris.api.event.player.PlayerKickEvent;
import org.lunaris.api.event.player.PlayerPreLoginEvent;
import org.lunaris.entity.LPlayer;
import org.lunaris.jwt.EncryptionHandler;
import org.lunaris.jwt.EncryptionRequestForger;
import org.lunaris.network.PacketHandler;
import org.lunaris.network.PlayerConnection;
import org.lunaris.network.PlayerConnectionState;
import org.lunaris.network.packet.Packet01Login;
import org.lunaris.network.packet.Packet02PlayStatus;
import org.lunaris.network.packet.Packet03EncryptionRequest;
import org.lunaris.network.packet.Packet06ResourcePacksInfo;

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
        if (name.length() < 3 || name.length() > 16)
            valid = false;
        else {
            if (name.contains(" ")) {
                connection.disconnect("We don't allow spaces in names, sorreh");
                return;
            }
            for (char c : name.toLowerCase().toCharArray())
                if (!(c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '-' || c == '_')) {
                    valid = false;
                    break;
                }
        }
        if (!valid) {
            connection.disconnect("Your nickname is invalid");
            return;
        }
        sync(() -> {
            LunarisServer server = getServer();
            LPlayer player = server.getPlayerProvider().createPlayer(packet, connection);
            if (server.getOnlinePlayers().size() >= server.getServerSettings().getMaxPlayersOnServer()) {
                PlayerKickEvent event = new PlayerKickEvent(player, "The server is full");
                event.setReasonType(PlayerKickEvent.ReasonType.SERVER_IS_FULL);
                server.getEventManager().call(event);
                if (!event.isCancelled()) {
                    player.disconnect(event.getReason());
                    return;
                }
            }
            PlayerPreLoginEvent event = new PlayerPreLoginEvent(player);
            server.getEventManager().call(event);
            if (event.isCancelled()) {
                player.disconnect();
                return;
            }
            if (LunarisServer.getInstance().getServerSettings().isUsingEncryptedConnection()) {
                EncryptionHandler encryptor = new EncryptionHandler(LunarisServer.getInstance().getEncryptionKeyFactory());
                encryptor.supplyClientKey(packet.getClientPublicKey());
                if (encryptor.beginClientsideEncryption()) {
                    connection.setConnectionState(PlayerConnectionState.ENCRPYTION_INIT);
                    connection.setPacketHandler(ENCRYPTION_HANDLER);
                    connection.setEncryptionHandler(encryptor);
                    EncryptionRequestForger forger = new EncryptionRequestForger();
                    String encryptionRequestJWT = forger.forge(encryptor.getServerPublic(), encryptor.getServerPrivate(), encryptor.getClientSalt());
                    Packet03EncryptionRequest encryptionPacket = new Packet03EncryptionRequest(encryptionRequestJWT);
                    player.sendPacket(encryptionPacket);
                }
            } else {
                connection.setConnectionState(PlayerConnectionState.RESOURCE_PACK);
                connection.setPacketHandler(RESOURCES_HANDLER);
                player.sendPacket(new Packet02PlayStatus(Packet02PlayStatus.Status.LOGIN_SUCCESS));
                player.sendPacket(new Packet06ResourcePacksInfo());
            }
        });
    }

}
