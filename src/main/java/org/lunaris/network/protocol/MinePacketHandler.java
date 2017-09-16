package org.lunaris.network.protocol;

import org.lunaris.Lunaris;
import org.lunaris.entity.Player;
import org.lunaris.event.player.*;
import org.lunaris.network.NetworkManager;
import org.lunaris.resourcepacks.ResourcePackManager;
import org.lunaris.network.protocol.packet.*;
import org.lunaris.resourcepacks.ResourcePack;
import org.lunaris.world.Location;

import java.util.Collection;
import java.util.Set;

/**
 * Created by RINES on 13.09.17.
 */
public class MinePacketHandler {

    private final Lunaris server = Lunaris.getInstance();
    private final NetworkManager networkManager;

    public MinePacketHandler(NetworkManager manager) {
        this.networkManager = manager;
    }

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

    public void handle(Packet04ClientToServerHandshake packet) {

    }

    public void handle(Packet05Disconnect packet) {

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

    public void handle(Packet09Text packet) {
        if(packet.getType() == Packet09Text.MessageType.CHAT) {
            this.server.getLogger().info("Message: %s", packet.getMessage());
            this.server.getLogger().info("Source: %s", packet.getSource());
            for(String message : packet.getMessage().split("\n")) {
                if(message.trim().isEmpty() || message.length() > 250)
                    continue;
                PlayerChatAsyncEvent event = new PlayerChatAsyncEvent(packet.getPlayer(), message);
                this.server.getEventManager().call(event);
                if(event.isCancelled())
                    continue;
                this.server.broadcastMessage("<" + packet.getPlayer().getName() + "> " + event.getMessage());
            }
        }else
            this.server.getLogger().info("Unknown type from client with chat packet: %s", packet.getType().name());
    }

    public void handle(Packet13MovePlayer packet) {
        Player player = packet.getPlayer();
        Location loc = player.getLocation();
        loc.setYaw(packet.getYaw());
        loc.setPitch(packet.getPitch());
        loc.setComponents(packet.getX(), packet.getY(), packet.getZ());
        //set on ground
        this.server.getScheduler().addSyncTask(() -> {
            PlayerMoveEvent event = new PlayerMoveEvent(player, packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
            this.server.getEventManager().call(event);
            if(event.isCancelled())
                return;
            Collection<Player> players = loc.getWorld().getApplicablePlayers(loc);
            players.remove(player);
            this.networkManager.sendPacket(players, packet);
        });
    }

    public void handle(Packet24PlayerAction packet) {
        Player p = packet.getPlayer();
        this.server.getLogger().info("Got action %s", packet.getAction().name());
        switch(packet.getAction()) {
            case RESPAWN: {
                if(!p.isOnline())
                    break;
                //respawn event
                Location spawn = p.getWorld().getSpawnLocation();
                p.sendPacket(new Packet2DRespawn((float) spawn.getX(), (float) spawn.getY(), (float) spawn.getZ()));

                break;
            }case CONTINUE_BREAK: {

//                this.server.getPlayerProvider().setupPlayer(p);
                break;
            }case START_SNEAK: {
                sync(() -> {
                    p.setState(packet);
                    this.networkManager.sendPacket(getApplicablePlayersWithout(p), packet);
                    PlayerSneakEvent event = new PlayerSneakEvent(p, PlayerSneakEvent.State.START_SNEAKING);
                    this.server.getEventManager().call(event);
                });
                break;
            }case STOP_SNEAK: {
                sync(() -> {
                    p.setState(packet);
                    this.networkManager.sendPacket(getApplicablePlayersWithout(p), packet);
                    PlayerSneakEvent event = new PlayerSneakEvent(p, PlayerSneakEvent.State.STOP_SNEAKING);
                    this.server.getEventManager().call(event);
                });
                break;
            }case START_SPRINT: {
                sync(() -> {
                    p.setState(packet);
                    this.networkManager.sendPacket(getApplicablePlayersWithout(p), packet);
                    PlayerSprintEvent event = new PlayerSprintEvent(p, PlayerSprintEvent.State.START_SPRINTING);
                    this.server.getEventManager().call(event);
                });
                break;
            }case STOP_SPRINT: {
                sync(() -> {
                    p.setState(packet);
                    this.networkManager.sendPacket(getApplicablePlayersWithout(p), packet);
                    PlayerSprintEvent event = new PlayerSprintEvent(p, PlayerSprintEvent.State.STOP_SPRINTING);
                    this.server.getEventManager().call(event);
                });
                break;
            }
            case JUMP: {
                sync(() -> {
                    this.networkManager.sendPacket(getApplicablePlayersWithout(p), packet);
                    PlayerJumpEvent event = new PlayerJumpEvent(p);
                    this.server.getEventManager().call(event);
                });
                break;
            }
        }
    }

    public void handle(Packet45RequestChunkRadius packet) {
        int value = Math.min(packet.getRadius(), this.server.getServerSettings().getChunksView());
        packet.getPlayer().sendPacket(new Packet46ChunkRadiusUpdate(value));
        packet.getPlayer().setChunksView(value);
    }

    private Collection<Player> getApplicablePlayersWithout(Player p) {
        Collection<Player> players = p.getWorld().getApplicablePlayers(p.getLocation());
        players.remove(p);
        return players;
    }

    private void sync(Runnable run) {
        this.server.getScheduler().addSyncTask(run);
    }

}
