package org.lunaris.network.handler;

import org.lunaris.LunarisServer;
import org.lunaris.api.entity.Gamemode;
import org.lunaris.api.event.player.*;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.world.Block;
import org.lunaris.api.world.Location;
import org.lunaris.block.LBlock;
import org.lunaris.entity.LEntity;
import org.lunaris.entity.LLivingEntity;
import org.lunaris.entity.LPlayer;
import org.lunaris.entity.data.BlockBreakingData;
import org.lunaris.entity.data.EntityDataFlag;
import org.lunaris.inventory.transaction.*;
import org.lunaris.material.LItemHandle;
import org.lunaris.network.NetworkManager;
import org.lunaris.network.PacketHandler;
import org.lunaris.network.packet.*;
import org.lunaris.world.BlockVector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xtrafrancyz
 */
public class IngameHandler extends PacketHandler {

    private static final long PLAYER_USE_DELAY = 160L;

    private final LunarisServer server;
    private final NetworkManager networkManager;
    
    public IngameHandler() {
        this.server = LunarisServer.getInstance();
        this.networkManager = LunarisServer.getInstance().getNetworkManager();
    }
    
    @Override
    protected void registerPacketHandlers() {
        addHandler(Packet05Disconnect.class, this::handleDisconnect);
        addHandler(Packet09Text.class, this::handleText);
        addHandler(Packet13MovePlayer.class, this::handlePlayerMovement);
        addHandler(Packet18LevelSoundEvent.class, this::handleLevelSound);
        addHandler(Packet1EInventoryTransaction.class, this::handleInventoryTransaction);
        addHandler(Packet1FMobEquipment.class, this::handleMobEquipment);
        addHandler(Packet24PlayerAction.class, this::handlePlayerAction);
        addHandler(Packet2CAnimate.class, this::handleAnimation);
        addHandler(Packet2FContainerClose.class, this::handleContainerClose);
        addHandler(Packet30PlayerHotbar.class, this::handlePlayerHotbar);
        addHandler(Packet45RequestChunkRadius.class, this::handleChunkRadiusRequest);
        addHandler(Packet4DCommandRequest.class, this::handleCommandRequest);
    }
    
    private void handleDisconnect(Packet05Disconnect packet, long time) {
        packet.getConnection().disconnect("Disconnected");
    }
    
    private void handleText(Packet09Text packet, long time) {
        if (packet.getType() == Packet09Text.MessageType.CHAT) {
            for (String message : packet.getMessage().split("\n")) {
                if (message.trim().isEmpty() || message.length() > 250) {
                    continue;
                }
                PlayerChatAsyncEvent event = new PlayerChatAsyncEvent(packet.getConnection().getPlayer(), message);
                this.server.getEventManager().call(event);
                if (event.isCancelled()) {
                    continue;
                }
                this.server.broadcastMessage("<" + packet.getConnection().getPlayer().getName() + "> " + event.getMessage());
            }
        } else {
            this.server.getLogger().info("Unknown type from client with chat packet: %s", packet.getType().name());
        }
    }

    private void handlePlayerMovement(Packet13MovePlayer packet, long time) {
        LPlayer player = packet.getConnection().getPlayer();
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

    private void handleLevelSound(Packet18LevelSoundEvent packet, long time) {
        sync(() -> packet.getConnection().getPlayer().sendPacketToWatchers(packet));
    }

    private void handleMobEquipment(Packet1FMobEquipment packet, long time) {
        sync(() -> {
            LPlayer p = packet.getConnection().getPlayer();
            ItemStack given = packet.getItem();
            ItemStack has = p.getInventory().getItem(packet.getHotbarSlot());
            if(!has.isSimilar(given)) {
                this.server.getLogger().warn("%s tried to equip item that is not in slot %d in inventory %d: %s vs %s", p.getName(), packet.getHotbarSlot(), packet.getInventoryId(), given.toString(), has.toString());
                p.getInventory().sendContents(p);
                return;
            }
            p.setDataFlag(false, EntityDataFlag.ACTION, false, true);
            p.getInventory().equipItem0(packet.getHotbarSlot());
            Set<LPlayer> players = new HashSet<>(LunarisServer.getInstance().getOnlinePlayers());
            players.remove(p);
            this.networkManager.sendPacket(players, packet);
        });
    }

    private void handlePlayerAction(Packet24PlayerAction packet, long time) {
        LPlayer p = packet.getConnection().getPlayer();
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
                    LunarisServer.getInstance().getEventManager().call(respawn);
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

    private void handleAnimation(Packet2CAnimate packet, long time) {
        sync(() -> packet.getConnection().getPlayer().sendPacketToWatchers(packet));
    }

    private void handleContainerClose(Packet2FContainerClose packet, long time) {
        sync(() -> packet.getConnection().getPlayer().getInventoryManager().closeInventory(packet.getInventoryID()));
    }

    private void handlePlayerHotbar(Packet30PlayerHotbar packet, long time) {
        if(packet.getInventoryId() != InventorySection.INVENTORY.getId())
            return;
        sync(() -> packet.getConnection().getPlayer().getInventory().equipItem0(packet.getActiveSlot()));
    }

    private void handleChunkRadiusRequest(Packet45RequestChunkRadius packet, long time) {
        int value = Math.min(packet.getRadius(), this.server.getServerSettings().getChunksView());
        LPlayer p = packet.getConnection().getPlayer();
        p.sendPacket(new Packet46ChunkRadiusUpdate(value));
        p.setChunksView(value);
    }

    private void handleCommandRequest(Packet4DCommandRequest packet, long time) {
        sync(() -> {
            LPlayer player = packet.getConnection().getPlayer();
            PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(player, packet.command);
            server.getEventManager().call(event);
            if (event.isCancelled())
                return;
            server.getCommandManager().handle(event.getCommand(), player);
        });
    }

    private void handleInventoryTransaction(Packet1EInventoryTransaction packet, long time) {
        sync(() -> {
            LPlayer player = packet.getConnection().getPlayer();
            List<InventoryAction> actions = new ArrayList<>();
            for (InventoryActionData actionData : packet.getActions()) {
                InventoryAction action = actionData.toInventoryAction(player);
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
                                    long passed = time - bdata.getBreakStartTime() + bdata.getOvertime();
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

}
