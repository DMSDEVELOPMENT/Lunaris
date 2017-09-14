package org.lunaris.network.protocol;

import org.lunaris.Lunaris;
import org.lunaris.entity.Player;
import org.lunaris.event.player.PlayerKickEvent;
import org.lunaris.event.player.PlayerPreLoginEvent;
import org.lunaris.network.NetworkManager;
import org.lunaris.resourcepacks.ResourcePackManager;
import org.lunaris.network.protocol.packet.*;
import org.lunaris.resourcepacks.ResourcePack;

/**
 * Created by RINES on 13.09.17.
 */
public class MinePacketHandler {

    private final Lunaris server = Lunaris.getInstance();

    public void handle(Packet01Login packet) {
        Player player = packet.getPlayer();
        if(player.getProtocolVersion() != NetworkManager.SUPPORTED_CLIENT_PROTOCOL_VERSION) {
            if(player.getProtocolVersion() < NetworkManager.SUPPORTED_CLIENT_PROTOCOL_VERSION) {
//                player.sendPacket(new Packet02PlayStatus(Packet02PlayStatus.Status.LOGIN_FAILED_CLIENT));
                player.disconnect("Your client is outdated\nWe support version " + this.server.getSupportedClientVersion());
            }else {
//                player.sendPacket(new Packet02PlayStatus(Packet02PlayStatus.Status.LOGIN_FAILED_SERVER));
                player.disconnect("Our server is outdated\nWe support version " + this.server.getSupportedClientVersion());
            }
            return;
        }
        boolean valid = true;
        String name = player.getName();
        if(name.length() < 3 || name.length() > 16)
            valid = false;
        else {
            if(name.contains(" ")) {
                player.disconnect("We don't allow spaces, sorreh");
                return;
            }
            for(char c : name.toLowerCase().toCharArray())
                if(!(c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '-' || c == '_')) {
                    valid = false;
                    break;
                }
        }
        if(!valid) {
            player.disconnect("Your nickname is invalid");
            return;
        }
        this.server.getScheduler().addSyncTask(() -> {
            if(this.server.getOnlinePlayers().size() >= this.server.getServerSettings().getMaxPlayersOnServer()) {
                PlayerKickEvent event = new PlayerKickEvent(player, "The server is full");
                event.setReasonType(PlayerKickEvent.ReasonType.SERVER_IS_FULL);
                this.server.getEventManager().call(event);
                if(!event.isCancelled()) {
                    player.disconnect(event.getReason());
                    return;
                }
            }
            PlayerPreLoginEvent event = new PlayerPreLoginEvent(player);
            this.server.getEventManager().call(event);
            if(event.isCancelled()) {
                player.disconnect();
                return;
            }
            player.sendPacket(new Packet02PlayStatus(Packet02PlayStatus.Status.LOGIN_SUCCESS));
            player.sendPacket(new Packet06ResourcePacksInfo());
        });
    }

    public void handle(Packet02PlayStatus packet) {

    }

    public void handle(Packet03ServerToClientHandshake packet) {

    }

    public void handle(Packet04ClientToServerHandshake packet) {

    }

    public void handle(Packet05Disconnect packet) {

    }

    public void handle(Packet06ResourcePacksInfo packet) {

    }

    public void handle(Packet07ResourcePackStack packet) {

    }

    public void handle(Packet08ResourcePackResponse packet) {
        Player player = packet.getPlayer();
        switch(packet.getResponseStatus()) {
            case Packet08ResourcePackResponse.STATUS_REFUSED: {
                player.disconnect("Resources refused");
                break;
            }case Packet08ResourcePackResponse.STATUS_SEND_PACKS: {
                for(String id : packet.getPackIds()) {
                    ResourcePack resourcePack = this.server.getResourcePackManager().getResourcePack(id);
                    if(resourcePack == null) {
                        player.disconnect("Unknown resource pack requested");
                        break;
                    }
                    player.sendPacket(new Packet52ResourcePackDataInfo(
                            resourcePack.getPackId(),
                            1 << 20,
                            resourcePack.getPackSize() / (1 << 20),
                            resourcePack.getPackSize(),
                            resourcePack.getSha256()
                    ));
                }
                break;
            }case Packet08ResourcePackResponse.STATUS_HAVE_ALL_PACKS: {
                ResourcePackManager manager = this.server.getResourcePackManager();
                player.sendPacket(new Packet07ResourcePackStack(manager.isResourcePackForced(), manager.getResourceStack()));
                break;
            }case Packet08ResourcePackResponse.STATUS_COMPLETED: {
                this.server.getPlayerProvider().addPlayerToGame(player);
                break;
            }default: {
                player.disconnect("Unknown resources response result");
                break;
            }
        }
    }

    public void handle(Packet52ResourcePackDataInfo packet) {}

    public void handle(PacketFFBatch packet) {}

}
