package org.lunaris.network.handler;

import org.lunaris.LunarisServer;
import org.lunaris.api.event.player.PlayerChatAsyncEvent;
import org.lunaris.entity.LPlayer;
import org.lunaris.network.NetworkManager;
import org.lunaris.network.PacketHandler;
import org.lunaris.network.PlayerConnection;
import org.lunaris.network.packet.Packet05Disconnect;
import org.lunaris.network.packet.Packet07ResourcePackStack;
import org.lunaris.network.packet.Packet08ResourcePackResponse;
import org.lunaris.network.packet.Packet09Text;
import org.lunaris.network.packet.Packet52ResourcePackDataInfo;
import org.lunaris.resourcepacks.ResourcePack;
import org.lunaris.resourcepacks.ResourcePackManager;

/**
 * @author xtrafrancyz
 */
public class IngameHandler extends PacketHandler {
    private final LunarisServer server;
    private final NetworkManager networkManager;
    
    public IngameHandler() {
        this.server = LunarisServer.getInstance();
        this.networkManager = LunarisServer.getInstance().getNetworkManager();
    }
    
    @Override
    protected void registerPacketHandlers() {
        addHandler(Packet05Disconnect.class, this::handleDisconnect);
        addHandler(Packet08ResourcePackResponse.class, this::handleResourcePackResponse);
    }
    
    private void handleDisconnect(Packet05Disconnect packet, long time) {
        packet.getConnection().disconnect("Disconnected");
    }
    
    private void handleResourcePackResponse(Packet08ResourcePackResponse packet, long time) {
        PlayerConnection connection = packet.getConnection();
        switch(packet.getResponseStatus()) {
            case Packet08ResourcePackResponse.STATUS_REFUSED: {
                connection.disconnect("Resources refused");
                break;
            }case Packet08ResourcePackResponse.STATUS_SEND_PACKS: {
                for(String id : packet.getPackIds()) {
                    ResourcePack resourcePack = this.server.getResourcePackManager().getResourcePack(id);
                    if(resourcePack == null) {
                        connection.disconnect("Unknown resource pack requested");
                        break;
                    }
                    networkManager.sendPacket(connection.getPlayer(), new Packet52ResourcePackDataInfo(
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
                networkManager.sendPacket(connection.getPlayer(), new Packet07ResourcePackStack(manager.isResourcePackForced(), manager.getResourceStack()));
                break;
            }case Packet08ResourcePackResponse.STATUS_COMPLETED: {
                sync(() -> this.server.getPlayerProvider().addPlayerToGame(connection.getPlayer()));
                break;
            }default: {
                connection.disconnect("Unknown resources response result");
                break;
            }
        }
    }
    
    private void handleText(Packet09Text packet, long time) {
        if(packet.getType() == Packet09Text.MessageType.CHAT) {
            for(String message : packet.getMessage().split("\n")) {
                if(message.trim().isEmpty() || message.length() > 250)
                    continue;
                PlayerChatAsyncEvent event = new PlayerChatAsyncEvent(packet.getConnection().getPlayer(), message);
                this.server.getEventManager().call(event);
                if(event.isCancelled())
                    continue;
                this.server.broadcastMessage("<" + packet.getConnection().getPlayer().getName() + "> " + event.getMessage());
            }
        }else
            this.server.getLogger().info("Unknown type from client with chat packet: %s", packet.getType().name());
    }
}
