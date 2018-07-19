package org.lunaris.network.handler;

import org.lunaris.entity.LPlayer;
import org.lunaris.network.PacketHandler;
import org.lunaris.network.PlayerConnectionState;
import org.lunaris.network.packet.Packet07ResourcePackStack;
import org.lunaris.network.packet.Packet08ResourcePackResponse;
import org.lunaris.network.packet.Packet52ResourcePackDataInfo;
import org.lunaris.resourcepacks.ResourcePack;
import org.lunaris.resourcepacks.ResourcePackManager;

/**
 * Created by k.shandurenko on 20.07.2018
 */
public class ResourcesHandler extends PacketHandler {
    @Override
    protected void registerPacketHandlers() {
        addHandler(Packet08ResourcePackResponse.class, this::handle);
    }

    private void handle(Packet08ResourcePackResponse packet, long time) {
        LPlayer player = packet.getConnection().getPlayer();
        switch (packet.getResponseStatus()) {
            case Packet08ResourcePackResponse.STATUS_REFUSED: {
                player.disconnect("Resources refused");
                break;
            }
            case Packet08ResourcePackResponse.STATUS_SEND_PACKS: {
                for(String id : packet.getPackIds()) {
                    ResourcePack resourcePack = getServer().getResourcePackManager().getResourcePack(id);
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
            }
            case Packet08ResourcePackResponse.STATUS_HAVE_ALL_PACKS: {
                ResourcePackManager manager = getServer().getResourcePackManager();
                player.sendPacket(new Packet07ResourcePackStack(manager.isResourcePackForced(), manager.getResourceStack()));
                break;
            }
            case Packet08ResourcePackResponse.STATUS_COMPLETED: {
                player.getConnection().setConnectionState(PlayerConnectionState.LOGIN);
                player.getConnection().setPacketHandler(new IngameHandler());
                sync(() -> {
                    getServer().getPlayerProvider().addPlayerToGame(player);
                    player.getConnection().setConnectionState(PlayerConnectionState.PLAYING);
                });
                break;
            }
        }
    }

}
