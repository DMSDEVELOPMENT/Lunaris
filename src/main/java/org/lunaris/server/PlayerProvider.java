package org.lunaris.server;

import org.lunaris.entity.Player;
import org.lunaris.entity.data.Attribute;
import org.lunaris.entity.data.EntityDataFlag;
import org.lunaris.event.player.PlayerDisconnectEvent;
import org.lunaris.event.player.PlayerJoinEvent;
import org.lunaris.event.player.PlayerLoginEvent;
import org.lunaris.network.protocol.packet.*;
import org.lunaris.network.raknet.session.RakNetClientSession;
import org.lunaris.world.Location;
import org.lunaris.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by RINES on 13.09.17.
 */
public class PlayerProvider {

    private final Map<String, Player> playersByNames = new HashMap<>();
    private final Map<UUID, Player> playersByUUIDs = new HashMap<>();
    private final Map<String, Player> byHostAddress = new ConcurrentHashMap<>();
    private final IServer server;
    private final EntityProvider entityProvider;
    private final Scheduler scheduler;

    public PlayerProvider(IServer server) {
        this.server = server;
        this.entityProvider = server.getEntityProvider();
        this.scheduler = server.getScheduler();
    }

    public Player createPlayer(Packet01Login packet, RakNetClientSession session) {
        Player player = new Player(this.entityProvider.getNextEntityID(), session, packet);
        this.byHostAddress.put(session.getAddress().getAddress().getHostAddress(), player);
        this.server.getLogger().info("%s (%s) is logging in..", player.getName(), player.getAddress());
        return player;
    }

    public void addPlayerToGame(Player player) {
        //check whitelist
        BanChecker checker = this.server.getBanChecker();
        if(checker.isNameBanned(player.getName()) || checker.isUUIDBanned(player.getClientUUID())) {
            player.disconnect("You have been banned");
            return;
        }
        if(checker.isAddressBanned(player.getAddress())) {
            player.disconnect("Your IP address have been banned");
            return;
        }
        Player another = getPlayer(player.getName());
        if(another != null) {
            another.disconnect("You logged in from another location");
            return;
        }
        another = getPlayer(player.getClientUUID());
        if(another != null) {
            another.disconnect("You logged in from another location");
            return;
        }
        Location loc = this.server.getWorldProvider().getWorld(0).getSpawnLocation();
        player.setLocation(loc);
        PlayerLoginEvent event = new PlayerLoginEvent(player);
        this.server.getEventManager().call(event);
        if(event.isCancelled()) {
            player.disconnect();
            return;
        }
        player.setIngameState(Player.IngameState.ONLINE);
        this.playersByNames.put(player.getName(), player);
        this.playersByUUIDs.put(player.getClientUUID(), player);
        Packet0BStartGame startGame = new Packet0BStartGame();
        startGame.entityUniqueId = startGame.entityRuntimeId = player.getEntityID();
        startGame.gamemode = startGame.playerGamemode = this.server.getServerSettings().getDefaultGamemode().ordinal();
        startGame.x = (float) loc.getX();
        startGame.y = (float) loc.getY();
        startGame.z = (float) loc.getZ();
        startGame.yaw = (float) loc.getYaw();
        startGame.pitch = (float) loc.getPitch();
        startGame.seed = -1;
        startGame.dimension = loc.getWorld().getDimension().getId();
        startGame.difficulty = loc.getWorld().getDifficulty().getId();
        Location spawn = loc.getWorld().getSpawnLocation();
        startGame.spawnX = spawn.getBlockX();
        startGame.spawnY = spawn.getBlockY();
        startGame.spawnZ = spawn.getBlockZ();
        startGame.hasAchievementsDisabled = true;
        startGame.dayCycleStopTime = -1;
        startGame.eduMode = false;
        startGame.rainLevel = 0;
        startGame.lightningLevel = 0;
        startGame.commandsEnabled = true;
        startGame.levelId = "";
        startGame.worldName = this.server.getServerSettings().getServerName();
        startGame.generator = 1; //0 old 1 infinity 2 flat
        player.sendPacket(new Packet28SetEntityMotion(-1, 0F, 0F, 0F));
        player.setDataFlag(false, EntityDataFlag.CAN_SHOW_NAMETAG, true, false);
        player.setDataFlag(false, EntityDataFlag.ALWAYS_SHOW_NAMETAG, true, false);
        player.setDataFlag(false, EntityDataFlag.CAN_CLIMB, true, false);
        player.setDataFlag(false, EntityDataFlag.BREATHING, true, false);
        player.setDataFlag(false, EntityDataFlag.GRAVITY, true, false);
        player.sendPacket(new Packet27SetEntityData(-1, player.getDataProperties()));
        player.sendPacket(new Packet1EUpdateAttributes(
                -1,
                player.getAttribute(Attribute.MAX_HEALTH),
                player.getAttribute(Attribute.MAX_HUNGER),
                player.getAttribute(Attribute.MOVEMENT_SPEED),
                player.getAttribute(Attribute.EXPERIENCE_LEVEL),
                player.getAttribute(Attribute.EXPERIENCE)
        ));
        player.sendPacket(startGame);
//        player.sendPacket(new Packet2BSetSpawnPosition(Packet2BSetSpawnPosition.SpawnType.WORLD_SPAWN, startGame.spawnX, startGame.spawnY, startGame.spawnZ, false));
//        player.sendPacket(new Packet13MovePlayer(player));
        player.sendPacket(new Packet0ASetTime(loc.getWorld().getTime()));
        sendAdventureSettings(player);
        player.sendPacket(new Packet2DRespawn((float) loc.getX(), (float) loc.getY(), (float) loc.getZ()));
//        player.sendPacket(new Packet27SetEntityData(player.getEntityID(), player.getDataProperties()));
//        player.sendPacket(new Packet28SetEntityMotion(-1, 0F, 0F, 0F));
//        player.sendPacket(new Packet1EUpdateAttributes(
//                player.getEntityID(),
//                player.getAttribute(Attribute.MAX_HEALTH),
//                player.getAttribute(Attribute.MAX_HUNGER),
//                player.getAttribute(Attribute.MOVEMENT_SPEED),
//                player.getAttribute(Attribute.EXPERIENCE_LEVEL),
//                player.getAttribute(Attribute.EXPERIENCE)
//        ));
        player.sendPacket(new Packet0ASetTime(player.getWorld().getTime()));
        player.sendPacket(new Packet3BSetCommandsEnabled(true));
        player.sendPacket(new Packet3FPlayerList(Packet3FPlayerList.Type.ADD, this.server.getOnlinePlayers().stream().map(Packet3FPlayerList.Entry::new).toArray(Packet3FPlayerList.Entry[]::new)));


        player.sendPacket(new Packet02PlayStatus(Packet02PlayStatus.Status.PLAYER_RESPAWN));
        player.getWorld().addPlayerToWorld(player);
        player.sendPacket(new Packet28SetEntityMotion(player.getEntityID(), 0F, 0F, 0F));
        player.sendPacket(new Packet13MovePlayer(player));
        PlayerJoinEvent joinEvent = new PlayerJoinEvent(player);
        this.server.getEventManager().call(joinEvent);
        player.sendPacket(new Packet27SetEntityData(-1, player.getDataProperties()));
        player.sendPacket(new Packet1EUpdateAttributes(
                -1,
                player.getAttribute(Attribute.MAX_HEALTH),
                player.getAttribute(Attribute.MAX_HUNGER),
                player.getAttribute(Attribute.MOVEMENT_SPEED),
                player.getAttribute(Attribute.EXPERIENCE_LEVEL),
                player.getAttribute(Attribute.EXPERIENCE)
        ));
        player.sendPacket(new Packet0ASetTime(loc.getWorld().getTime()));
        this.server.getScheduler().schedule(() -> {
        }, 10, TimeUnit.SECONDS);
    }

    private void sendAdventureSettings(Player player) {
        Packet37AdventureSettings packet = new Packet37AdventureSettings();
        packet.flags = this.server.getServerSettings().getAdventureSettingsFlag();
        packet.worldImmutable = false;
        packet.autoJump = false;
        packet.allowFlight = false;
        packet.noClip = false;
        packet.isFlying = false;
        packet.noPvp = false;
        packet.noPvm = false;
        packet.noMvp = false;
        packet.muted = false;
        packet.userPermission = Packet37AdventureSettings.PERMISSION_LEVEL_MEMBER;
        player.sendPacket(packet);
    }

    public Player getPlayer(String name) {
        return this.playersByNames.get(name);
    }

    public Player getPlayer(UUID uuid) {
        return this.playersByUUIDs.get(uuid);
    }

    public Player getPlayer(RakNetClientSession session) {
        return this.byHostAddress.get(session.getAddress().getAddress().getHostAddress());
    }

    public Player removePlayer(RakNetClientSession session) {
        Player player = this.byHostAddress.remove(session.getAddress().getAddress().getHostAddress());
        if(player == null)
            return null;
        boolean wasOnline = player.isOnline();
        player.setIngameState(Player.IngameState.DISCONNECTING);
        this.scheduler.addSyncTask(() -> {
            PlayerDisconnectEvent event = new PlayerDisconnectEvent(player);
            this.server.getEventManager().call(event);
            this.playersByNames.remove(player.getName());
            this.playersByUUIDs.remove(player.getClientUUID());
            if(wasOnline) {
                player.getWorld().removePlayerFromWorld(player);
            }
            this.server.getLogger().info("%s disconnected: %s", player.getName(), player.getDisconnectingReason());
        });
        return player;
    }

    public Collection<Player> getOnlinePlayers() {
        return this.playersByUUIDs.values();
    }

}
