package org.lunaris.server;

import org.lunaris.LunarisServer;
import org.lunaris.api.server.Scheduler;
import org.lunaris.entity.LPlayer;
import org.lunaris.entity.data.Attribute;
import org.lunaris.entity.data.EntityDataFlag;
import org.lunaris.entity.misc.LPermission;
import org.lunaris.api.event.player.PlayerDisconnectEvent;
import org.lunaris.api.event.player.PlayerJoinEvent;
import org.lunaris.api.event.player.PlayerLoginEvent;
import org.lunaris.network.protocol.packet.*;
import org.lunaris.network.raknet.session.RakNetClientSession;
import org.lunaris.api.world.Location;
import org.lunaris.world.LWorld;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by RINES on 13.09.17.
 */
public class PlayerProvider {

    private final Map<String, LPlayer> playersByNames = new HashMap<>();
    private final Map<UUID, LPlayer> playersByUUIDs = new HashMap<>();
    private final Map<RakNetClientSession, LPlayer> playersBySessions = new ConcurrentHashMap<>();
    private final LunarisServer server;
    private final Scheduler scheduler;

    public PlayerProvider(LunarisServer server) {
        this.server = server;
        this.scheduler = server.getScheduler();
    }

    public LPlayer createPlayer(Packet01Login packet, RakNetClientSession session) {
        LPlayer player = this.server.getEntityProvider().createPlayer(packet, session);
        player.setPermission(LPermission.OPERATOR);
        this.playersBySessions.put(session, player);
        this.server.getLogger().info("%s (%s) is logging in..", player.getName(), player.getAddress());
        return player;
    }

    public void addPlayerToGame(LPlayer player) {
        LPlayer another = getPlayer(player.getName());
        if(another != null) {
            another.disconnect("You logged in from another location");
            return;
        }
        another = getPlayer(player.getUUID());
        if(another != null) {
            another.disconnect("You logged in from another location");
            return;
        }
        Location loc = this.server.getWorldProvider().getWorld(0).getSpawnLocation();
        player.teleport(loc);
        PlayerLoginEvent event = new PlayerLoginEvent(player);
        this.server.getEventManager().call(event);
        if(event.isCancelled()) {
            player.disconnect();
            return;
        }
        player.setIngameState(LPlayer.IngameState.ONLINE);
        this.playersByNames.put(player.getName().toLowerCase(), player);
        this.playersByUUIDs.put(player.getUUID(), player);
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
        startGame.difficulty = ((LWorld) loc.getWorld()).getDifficulty().getId();
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
        player.setDisplayNameVisible(true, true);
        player.setDisplayName(player.getName());
        player.setDataFlag(false, EntityDataFlag.CAN_CLIMB, true, false);
        player.setDataFlag(false, EntityDataFlag.BREATHING, true, false);
        player.setDataFlag(false, EntityDataFlag.GRAVITY, true, false);
        player.setDirtyMetadata(false);
        player.sendPacket(startGame);
        player.sendPacket(new Packet2DRespawn((float) loc.getX(), (float) loc.getY(), (float) loc.getZ()));
        player.sendPacket(new Packet0ASetTime(player.getWorld().getTime()));
        player.sendPacket(new Packet3BSetCommandsEnabled(true));
        this.server.getPlayerList().addPlayer(player);
        player.sendPacket(new Packet02PlayStatus(Packet02PlayStatus.Status.PLAYER_RESPAWN));
        player.sendPacket(new Packet27SetEntityData(player.getEntityID(), player.getDataProperties()));
        player.sendPacket(new Packet1DUpdateAttributes(
                player.getEntityID(),
                player.getAttribute(Attribute.MAX_HEALTH),
                player.getAttribute(Attribute.MAX_HUNGER),
                player.getAttribute(Attribute.MOVEMENT_SPEED),
                player.getAttribute(Attribute.EXPERIENCE_LEVEL),
                player.getAttribute(Attribute.EXPERIENCE)
        ));
        player.sendPacket(new Packet28SetEntityMotion(player.getEntityID(), 0F, 0F, 0F));
        player.getAdventureSettings().update();
        player.getWorld().addPlayerToWorld(player);
        player.sendAvailableCommands();
        PlayerJoinEvent joinEvent = new PlayerJoinEvent(player);
        this.server.getEventManager().call(joinEvent);
    }

    public LPlayer getPlayer(String name) {
        return this.playersByNames.get(name.toLowerCase());
    }

    public LPlayer getPlayer(UUID uuid) {
        return this.playersByUUIDs.get(uuid);
    }

    public LPlayer getPlayer(RakNetClientSession session) {
        return this.playersBySessions.get(session);
    }

    public LPlayer removePlayer(RakNetClientSession session) {
        LPlayer player = this.playersBySessions.remove(session);
        if(player == null)
            return null;
        boolean wasOnline = player.isOnline();
        player.setIngameState(LPlayer.IngameState.DISCONNECTING);
        this.scheduler.run(() -> {
            PlayerDisconnectEvent event = new PlayerDisconnectEvent(player);
            this.server.getEventManager().call(event);
            this.server.getPlayerList().removePlayer(player);
            this.playersByNames.remove(player.getName().toLowerCase());
            this.playersByUUIDs.remove(player.getUUID());
            if(wasOnline) {
                player.getWorld().removePlayerFromWorld(player);
            }
            this.server.getLogger().info("%s disconnected: %s", player.getName(), player.getDisconnectingReason());
        });
        return player;
    }

    public Collection<LPlayer> getOnlinePlayers() {
        return this.playersByUUIDs.values();
    }

    public Collection<LPlayer> getOnlinePlayersWithout(LPlayer... without) {
        Set<LPlayer> players = new HashSet<>();
        players.addAll(getOnlinePlayers());
        for(LPlayer player : without)
            players.remove(player);
        return players;
    }

    public Collection<LPlayer> getAllPlayers() {
        return this.playersBySessions.values();
    }

}
