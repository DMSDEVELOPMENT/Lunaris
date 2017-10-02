package org.lunaris.network.protocol;

import org.lunaris.Lunaris;
import org.lunaris.entity.Player;
import org.lunaris.entity.data.EntityDataFlag;
import org.lunaris.entity.data.Gamemode;
import org.lunaris.event.player.*;
import org.lunaris.inventory.transaction.*;
import org.lunaris.item.ItemStack;
import org.lunaris.network.NetworkManager;
import org.lunaris.resourcepacks.ResourcePackManager;
import org.lunaris.network.protocol.packet.*;
import org.lunaris.resourcepacks.ResourcePack;
import org.lunaris.world.Location;

import java.util.*;

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
        sync(() -> {
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
                sync(() -> this.server.getPlayerProvider().addPlayerToGame(player));
                break;
            }default: {
                player.disconnect("Unknown resources response result");
                break;
            }
        }
    }

    public void handle(Packet09Text packet) {
        if(packet.getType() == Packet09Text.MessageType.CHAT) {
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
        sync(() -> {
            player.moveTo(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
            player.recalculateCollisions();
            //set on ground
        });
    }

    public void handle(Packet18LevelSoundEvent packet) {
        Player p = packet.getPlayer();
        sync(() -> {
            Collection<Player> players = p.getWorld().getApplicablePlayers(p.getLocation());
            players.remove(p);
            this.networkManager.sendPacket(players, packet);
        });
    }

    public void handle(Packet1FMobEquipment packet) {
        sync(() -> {
            Set<Player> players = new HashSet<>(Lunaris.getInstance().getOnlinePlayers());
            players.remove(packet.getPlayer());
            this.networkManager.sendPacket(players, packet);
        });
    }

    public void handle(Packet24PlayerAction packet) {
        Player p = packet.getPlayer();
        switch(packet.getAction()) {
            case START_SNEAK: {
                sync(() -> {
                    p.setState(packet);
                    p.setDataFlag(false, EntityDataFlag.SNEAKING, true, true);
                    PlayerSneakEvent event = new PlayerSneakEvent(p, PlayerSneakEvent.State.START_SNEAKING);
                    this.server.getEventManager().call(event);
                });
                break;
            }case STOP_SNEAK: {
                sync(() -> {
                    p.setState(packet);
                    p.setDataFlag(false, EntityDataFlag.SNEAKING, false, true);
                    PlayerSneakEvent event = new PlayerSneakEvent(p, PlayerSneakEvent.State.STOP_SNEAKING);
                    this.server.getEventManager().call(event);
                });
                break;
            }case START_SPRINT: {
                sync(() -> {
                    p.setState(packet);
                    p.setDataFlag(false, EntityDataFlag.SPRINTING, true, true);
                    PlayerSprintEvent event = new PlayerSprintEvent(p, PlayerSprintEvent.State.START_SPRINTING);
                    this.server.getEventManager().call(event);
                });
                break;
            }case STOP_SPRINT: {
                sync(() -> {
                    p.setState(packet);
                    p.setDataFlag(false, EntityDataFlag.SPRINTING, false, true);
                    PlayerSprintEvent event = new PlayerSprintEvent(p, PlayerSprintEvent.State.STOP_SPRINTING);
                    this.server.getEventManager().call(event);
                });
                break;
            }case JUMP: {
                sync(() -> {
                    this.networkManager.sendPacket(getApplicablePlayersWithout(p), packet);
                    PlayerJumpEvent event = new PlayerJumpEvent(p);
                    this.server.getEventManager().call(event);
                });
                break;
            }case START_BREAK: {
                sync(() -> this.server.getWorldProvider().getBlockMaster().onBlockStartBreak(packet));
                break;
            }case CONTINUE_BREAK: {
                sync(() -> this.server.getWorldProvider().getBlockMaster().onBlockContinueBreak(packet));
                break;
            }case ABORT_BREAK: {
                sync(() -> this.server.getWorldProvider().getBlockMaster().onBlockAbortBreak(packet));
                break;
            }case STOP_BREAK: {
                sync(() -> this.server.getWorldProvider().getBlockMaster().onBlockStopBreak(packet));
                break;
            }case RESPAWN: {
                //somewhy never happens
                sync(() -> {
                    PlayerRespawnEvent respawn = new PlayerRespawnEvent(p, p.getWorld().getSpawnLocation());
                    Lunaris.getInstance().getEventManager().call(respawn);
                    p.respawn(respawn.getLocation());
                });
                break;
            }
            default: {
                this.server.getLogger().info("Got action %s", packet.getAction().name());
                break;
            }
        }
    }

    public void handle(Packet2CAnimate packet) {
        packet.getPlayer().getLocation().getChunk().sendPacket(packet);
    }

    public void handle(Packet45RequestChunkRadius packet) {
        int value = Math.min(packet.getRadius(), this.server.getServerSettings().getChunksView());
        packet.getPlayer().sendPacket(new Packet46ChunkRadiusUpdate(value));
        packet.getPlayer().setChunksView(value);
    }

    public void handle(Packet1EInventoryTransaction packet) {
        sync(() -> {
            Player player = packet.getPlayer();
            List<InventoryAction> actions = new ArrayList<>();
            for (InventoryActionData actionData : packet.getActions()) {
                InventoryAction action = actionData.toInventoryAction(packet.getPlayer());
                if (action == null)
                    continue;
                actions.add(action);
            }
            switch (packet.getType()) {
                case NORMAL: {
                    InventoryTransaction transaction = new BasicInventoryTransaction(player, actions);
                    if (!transaction.execute()) {
                        transaction.getInventories().forEach(inventory -> inventory.sendContents(player));
                        break;
                    }

                    break;
                }
                case MISMATCH: {
                    player.getInventoryManager().sendAllInventories();
                    break;
                }
                case USE_ITEM: {
                    UseItemData data = (UseItemData) packet.getData();
                    switch(data.getType()) {
                        case CLICK_BLOCK: {
                            player.setDataFlag(false, EntityDataFlag.ACTION, false, true);
                            if(true /*can interact with this location*/)
                                this.server.getWorldProvider().getBlockMaster().onRightClickBlock(player, data.getBlockPosition(), data.getBlockFace(), data.getClickPosition());
                            break;
                        }case BREAK_BLOCK: {

                            break;
                        }case CLICK_AIR: {

                            break;
                        }
                    }
                    break;
                }
                case USE_ITEM_ON_ENTITY: {

                    break;
                }
                case RELEASE_ITEM: {

                    break;
                }
                default: {
                    player.getInventory().sendContents(player);
                    break;
                }
            }
        });
    }

    public void handle(Packet31InventoryContent packet) {

    }

    public void handle(Packet32InventorySlot packet) {

    }

    private Collection<Player> getApplicablePlayersWithout(Player p) {
        Collection<Player> players = p.getWorld().getApplicablePlayers(p.getLocation());
        players.remove(p);
        return players;
    }

    private void sync(Runnable run) {
        this.server.getScheduler().run(run);
    }

}
