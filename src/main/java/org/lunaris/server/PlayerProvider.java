package org.lunaris.server;

import org.lunaris.Lunaris;
import org.lunaris.entity.Player;
import org.lunaris.entity.data.Attribute;
import org.lunaris.entity.data.EntityDataFlag;
import org.lunaris.event.player.PlayerDisconnectEvent;
import org.lunaris.event.player.PlayerJoinEvent;
import org.lunaris.event.player.PlayerLoginEvent;
import org.lunaris.network.protocol.packet.*;
import org.lunaris.network.raknet.session.RakNetClientSession;
import org.lunaris.world.Location;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by RINES on 13.09.17.
 */
public class PlayerProvider {

    private final Map<String, Player> playersByNames = new HashMap<>();
    private final Map<UUID, Player> playersByUUIDs = new HashMap<>();
    private final Map<RakNetClientSession, Player> playersBySessions = new ConcurrentHashMap<>();
    private final Lunaris server;
    private final EntityProvider entityProvider;
    private final Scheduler scheduler;

    public PlayerProvider(Lunaris server) {
        this.server = server;
        this.entityProvider = server.getEntityProvider();
        this.scheduler = server.getScheduler();
    }

    public Player createPlayer(Packet01Login packet, RakNetClientSession session) {
        Player player = new Player(this.entityProvider.getNextEntityID(), session, packet);
        this.playersBySessions.put(session, player);
        this.server.getLogger().info("%s (%s) is logging in..", player.getName(), player.getAddress());
        return player;
    }

    public void addPlayerToGame(Player player) {
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
        player.initializeLocation(loc);
        PlayerLoginEvent event = new PlayerLoginEvent(player);
        this.server.getEventManager().call(event);
        if(event.isCancelled()) {
            player.disconnect();
            return;
        }
        player.setIngameState(Player.IngameState.ONLINE);
        this.playersByNames.put(player.getName().toLowerCase(), player);
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
        PlayerJoinEvent joinEvent = new PlayerJoinEvent(player);
        this.server.getEventManager().call(joinEvent);
    }

    public Player getPlayer(String name) {
        return this.playersByNames.get(name.toLowerCase());
    }

    public Player getPlayer(UUID uuid) {
        return this.playersByUUIDs.get(uuid);
    }

    public Player getPlayer(RakNetClientSession session) {
        return this.playersBySessions.get(session);
    }

    public Player removePlayer(RakNetClientSession session) {
        Player player = this.playersBySessions.remove(session);
        if(player == null)
            return null;
        boolean wasOnline = player.isOnline();
        player.setIngameState(Player.IngameState.DISCONNECTING);
        this.scheduler.run(() -> {
            PlayerDisconnectEvent event = new PlayerDisconnectEvent(player);
            this.server.getEventManager().call(event);
            this.server.getPlayerList().removePlayer(player);
            this.playersByNames.remove(player.getName().toLowerCase());
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

    public Collection<Player> getOnlinePlayersWithout(Player... without) {
        Set<Player> players = new HashSet<>();
        players.addAll(getOnlinePlayers());
        for(Player player : without)
            players.remove(player);
        return players;
    }

    public Collection<Player> getAllPlayers() {
        return this.playersBySessions.values();
    }

}
