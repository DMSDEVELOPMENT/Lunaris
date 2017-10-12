package org.lunaris.network.protocol;

import org.lunaris.Lunaris;
import org.lunaris.api.world.Block;
import org.lunaris.block.LBlock;
import org.lunaris.entity.LEntity;
import org.lunaris.entity.LLivingEntity;
import org.lunaris.entity.LPlayer;
import org.lunaris.entity.data.BlockBreakingData;
import org.lunaris.entity.data.EntityDataFlag;
import org.lunaris.api.entity.Gamemode;
import org.lunaris.event.player.*;
import org.lunaris.inventory.transaction.*;
import org.lunaris.api.item.ItemStack;
import org.lunaris.jwt.EncryptionHandler;
import org.lunaris.jwt.EncryptionRequestForger;
import org.lunaris.material.LItemHandle;
import org.lunaris.network.NetworkManager;
import org.lunaris.network.util.ConnectionState;
import org.lunaris.resourcepacks.ResourcePackManager;
import org.lunaris.network.protocol.packet.*;
import org.lunaris.resourcepacks.ResourcePack;
import org.lunaris.world.BlockVector;
import org.lunaris.api.world.Location;

import java.util.*;

/**
 * Created by RINES on 13.09.17.
 */
public class MinePacketHandler {
    private static final long PLAYER_USE_DELAY = 160L;

    private final Lunaris server = Lunaris.getInstance();
    private final NetworkManager networkManager;
    private final EncryptionRequestForger FORGER = new EncryptionRequestForger();

    public MinePacketHandler(NetworkManager manager) {
        this.networkManager = manager;
    }

    public void handle(Packet01Login packet) {
        LPlayer player = packet.getPlayer();
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
        if(packet.getDisconnectReason() != null) {
            player.disconnect(packet.getDisconnectReason());
            return;
        }
        boolean valid = true;
        String name = player.getName();
        if(name.length() < 3 || name.length() > 16)
            valid = false;
        else {
            if(name.contains(" ")) {
                player.disconnect("We don't allow spaces in names, sorreh");
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
            if(Lunaris.getInstance().getServerSettings().isUsingEncryptedConnection()) {
                EncryptionHandler encryptor = new EncryptionHandler(Lunaris.getInstance().getEncryptionKeyFactory());
                encryptor.supplyClientKey(packet.getClientPublicKey());
                if(encryptor.beginClientsideEncryption()) {
                    player.getSession().setConnectionState(ConnectionState.ENCRYPTION_INIT);
                    player.getSession().setupEncryptor(encryptor);
                    String encryptionRequestJWT = FORGER.forge(encryptor.getServerPublic(), encryptor.getServerPrivate(), encryptor.getClientSalt());
                    Packet03EncryptionRequest encryptionPacket = new Packet03EncryptionRequest(encryptionRequestJWT);
                    player.sendPacket(encryptionPacket);
                }
            }else {
                player.getSession().setConnectionState(ConnectionState.LOGGED_IN);
                player.sendPacket(new Packet02PlayStatus(Packet02PlayStatus.Status.LOGIN_SUCCESS));
                player.sendPacket(new Packet06ResourcePacksInfo());
            }
        });
    }

    public void handle(Packet04EncryptionResponse packet) {
        LPlayer player = packet.getPlayer();
        player.getSession().setConnectionState(ConnectionState.LOGGED_IN);
        player.sendPacket(new Packet02PlayStatus(Packet02PlayStatus.Status.LOGIN_SUCCESS));
        player.sendPacket(new Packet06ResourcePacksInfo());
    }

    public void handle(Packet05Disconnect packet) {
        packet.getPlayer().disconnect("Disconnected");
    }

    public void handle(Packet08ResourcePackResponse packet) {
        LPlayer player = packet.getPlayer();
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
        LPlayer player = packet.getPlayer();
        sync(() -> {
            Location from = player.getLocation(), to = player.getLocation();
            to.setComponents(packet.getX(), packet.getY() - player.getEyeHeight(), packet.getZ());
            to.setYaw(packet.getYaw());
            to.setHeadYaw(packet.getHeadYaw());
            to.setPitch(packet.getPitch());
            if(to.getX() - from.getX() == 0 &&
                    to.getY() - from.getY() == 0 &&
                    to.getZ() - from.getZ() == 0 &&
                    to.getYaw() - from.getYaw() == 0 &&
                    to.getHeadYaw() - from.getHeadYaw() == 0 &&
                    to.getPitch() - from.getPitch() == 0)
                return;
            PlayerMoveEvent event = new PlayerMoveEvent(player, to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
            this.server.getEventManager().call(event);
            if(event.isCancelled())
                to = from;
            if(to.getX() != packet.getX() || to.getY() != packet.getY() - player.getEyeHeight() || to.getZ() != packet.getZ() ||
                    to.getYaw() != packet.getYaw() || to.getHeadYaw() != packet.getHeadYaw() || to.getPitch() != packet.getPitch()) {
                player.teleport(to);
            }
            player.setPositionAndRotation(to);
            float hwidth = player.getWidth() / 2;
            player.getBoundingBox().setBounds(
                    player.getX() - hwidth,
                    player.getY(),
                    player.getZ() - hwidth,
                    player.getX() + hwidth,
                    player.getY() + player.getHeight(),
                    player.getZ() + hwidth
            );
            boolean changeWorld = !to.getWorld().equals(from.getWorld());
            boolean changeXZ = (int) from.getX() != (int) to.getX() || (int) from.getZ() != (int) to.getZ();
            boolean changeY = (int) from.getY() != (int) to.getY();
            if(changeWorld || changeXZ || changeY) {
                Block block = from.getWorld().getBlockAt(from.getBlockX(), from.getBlockY(), from.getBlockZ());
                block.getHandle().onStepOff(block, player);
                block = to.getWorld().getBlockAt(to.getBlockX(), to.getBlockY(), to.getBlockZ());
                block.getHandle().onStepOn(block, player);
            }
        });
    }

    public void handle(Packet18LevelSoundEvent packet) {
        sync(() -> packet.getPlayer().sendPacketToWatchers(packet));
    }

    public void handle(Packet1FMobEquipment packet) {
        sync(() -> {
            LPlayer p = packet.getPlayer();
            ItemStack given = packet.getItem();
            ItemStack has = p.getInventory().getItem(packet.getHotbarSlot());
            if(!has.isSimilar(given)) {
                this.server.getLogger().warn("%s tried to equip item that is not in slot %d in inventory %d: %s vs %s", p.getName(), packet.getHotbarSlot(), packet.getInventoryId(), given.toString(), has.toString());
                p.getInventory().sendContents(p);
                return;
            }
            p.setDataFlag(false, EntityDataFlag.ACTION, false, true);
            p.getInventory().equipItem0(packet.getHotbarSlot());
            Set<LPlayer> players = new HashSet<>(Lunaris.getInstance().getOnlinePlayers());
            players.remove(packet.getPlayer());
            this.networkManager.sendPacket(players, packet);
        });
    }

    public void handle(Packet24PlayerAction packet) {
        LPlayer p = packet.getPlayer();
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
                    p.sendPacketToWatchers(packet);
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
        sync(() -> packet.getPlayer().sendPacketToWatchers(packet));
    }

    public void handle(Packet2FContainerClose packet) {
        sync(() -> packet.getPlayer().getInventoryManager().closeInventory(packet.getInventoryID()));
    }

    public void handle(Packet30PlayerHotbar packet) {
        if(packet.getInventoryId() != InventorySection.INVENTORY.getId())
            return;
        sync(() -> packet.getPlayer().getInventory().equipItem0(packet.getActiveSlot()));
    }

    public void handle(Packet45RequestChunkRadius packet) {
        int value = Math.min(packet.getRadius(), this.server.getServerSettings().getChunksView());
        packet.getPlayer().sendPacket(new Packet46ChunkRadiusUpdate(value));
        packet.getPlayer().setChunksView(value);
    }
    
    public void handle(Packet4DCommandRequest packet) {
        sync(() -> {
            LPlayer player = packet.getPlayer();
            PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(player, packet.command);
            server.getEventManager().call(event);
            if (event.isCancelled())
                return;
            server.getCommandManager().handle(event.getCommand(), player);
        });
    }

    public void handle(Packet1EInventoryTransaction packet) {
        sync(() -> {
            LPlayer player = packet.getPlayer();
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
                            this.server.getWorldProvider().getBlockMaster().onRightClickBlock(player, data.getBlockPosition(), data.getBlockFace(), data.getClickPosition());
                            return;
                        }case BREAK_BLOCK: {
                            BlockVector vec = data.getBlockPosition();
                            LBlock block = player.getWorld().getBlockAt(vec.getX(), vec.getY(), vec.getZ());
                            if(player.getGamemode() == Gamemode.CREATIVE)
                                this.server.getWorldProvider().getBlockMaster().processBlockBreak(player, block, false);
                            else {
                                if(!player.isBreakingBlock())
                                    player.sendPacket(new Packet15UpdateBlock(player.getWorld().getBlockAt(vec.getX(), vec.getY(), vec.getZ())));
                                else {
                                    BlockBreakingData bdata = player.getBlockBreakingData();
                                    long passed = System.currentTimeMillis() - bdata.getBreakStartTime() + bdata.getOvertime();
                                    long delta = bdata.getBlockBreakingTime() - passed;
                                    if(delta <= 100)
                                        this.server.getWorldProvider().getBlockMaster().processBlockBreak(player, block, true);
                                    else
                                        player.sendPacket(new Packet15UpdateBlock(block));
                                }
                            }
                            player.getBlockBreakingData().clear();
                            return;
                        }case CLICK_AIR: {

                            return;
                        }
                    }
                    break;
                }
                case USE_ITEM_ON_ENTITY: {
                    UseItemOnEntityData data = (UseItemOnEntityData) packet.getData();
                    LEntity entity = player.getWorld().getEntityById(data.getEntityID());
                    if(entity == null)
                        return;
                    if(!data.getItemInHand().equals(player.getInventory().getItemInHand())) {
                        player.getInventory().sendContents(player);
                        return;
                    }
                    if(player.getGamemode() == Gamemode.SPECTATOR)
                        return;
                    long time = System.currentTimeMillis();
                    if (time - player.getLastUseTime() < PLAYER_USE_DELAY)
                        return;
                    player.setLastUseTime(time);
                    ItemStack item = player.getInventory().getItemInHand();
                    switch(data.getType()) {
                        case INTERACT: {
                            PlayerInteractEntityEvent event = new PlayerInteractEntityEvent(player, entity);
                            event.call();
                            if(event.isCancelled())
                                return;
                            LItemHandle handle = item.getHandle().isBlock() ? null : (LItemHandle) item.getItemHandle();
                            if(handle != null && handle.canBeUsed() && handle.useOn(entity, item, player)) {
                                if(handle.getMaxDurability() > 0) {
                                    item.setData(item.getData() + 1);
                                    if(item.getData() >= handle.getMaxDurability())
                                        player.getInventory().setItemInHand(null);
                                    else
                                        player.getInventory().setItemInHand(item);
                                }
                            }
                            break;
                        }case ATTACK: {
                            if(!(entity instanceof LLivingEntity))
                                return;
                            double itemDamage = item.getHandle().getAttackDamage();
                            //check enchantments
                            //call events
                            ((LLivingEntity) entity).damage(player, itemDamage);
                            break;
                        }
                    }
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

    private void sync(Runnable run) {
        this.server.getScheduler().run(run);
    }

}
