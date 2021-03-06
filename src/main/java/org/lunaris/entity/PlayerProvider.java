package org.lunaris.entity;

import org.lunaris.LunarisServer;
import org.lunaris.api.event.player.PlayerDisconnectEvent;
import org.lunaris.api.event.player.PlayerJoinEvent;
import org.lunaris.api.event.player.PlayerLoginEvent;
import org.lunaris.api.server.Scheduler;
import org.lunaris.api.world.Location;
import org.lunaris.entity.LPlayer;
import org.lunaris.entity.data.Attribute;
import org.lunaris.entity.data.EntityDataFlag;
import org.lunaris.entity.misc.LPermission;
import org.lunaris.network.PlayerConnection;
import org.lunaris.network.packet.*;
import org.lunaris.world.LWorld;

import java.util.*;

/**
 * Created by RINES on 13.09.17.
 */
public class PlayerProvider {

    private final Map<String, LPlayer> playersByNames = new HashMap<>();
    private final Map<UUID, LPlayer> playersByUUIDs = new HashMap<>();
    private final LunarisServer server;

    public PlayerProvider(LunarisServer server) {
        this.server = server;
    }

    public LPlayer createPlayer(Packet01Login packet, PlayerConnection connection) {
        LPlayer player = this.server.getEntityProvider().createPlayer(packet, connection);
        connection.setPlayer(player);
        player.setPermission(LPermission.OPERATOR);
        this.server.getLogger().info("%s (%s) is logging in..", player.getName(), player.getAddress());
        return player;
    }

    public void addPlayerToGame(LPlayer player) {
        LPlayer another = getPlayer(player.getName());
        if (another != null) {
            another.disconnect("You logged in from another location");
            return;
        }
        another = getPlayer(player.getUUID());
        if (another != null) {
            another.disconnect("You logged in from another location");
            return;
        }
        Location loc = this.server.getWorldProvider().getWorld(0).getSpawnLocation();
        player.teleport(loc);
        PlayerLoginEvent event = new PlayerLoginEvent(player);
        this.server.getEventManager().call(event);
        if (event.isCancelled()) {
            player.disconnect();
            return;
        }
        this.playersByNames.put(player.getName().toLowerCase(), player);
        this.playersByUUIDs.put(player.getUUID(), player);

        setupPlayerMetadata(player);
        sendStartGamePacket(player, loc);
        player.sendPacket(new Packet02PlayStatus(Packet02PlayStatus.Status.RESPAWN));
        player.sendPacket(new Packet27SetEntityData(player.getEntityID(), player.getMetadata().getDataProperties()));
        player.sendPacket(new Packet2DRespawn((float) loc.getX(), (float) loc.getY(), (float) loc.getZ()));
        player.sendPacket(new Packet0AWorldTime(player.getWorld().getTime()));
        player.sendPacket(new Packet3CSetDifficulty(((LWorld) loc.getWorld()).getDifficulty()));
        player.sendPacket(new Packet3BSetCommandsEnabled(true));
        player.getAdventureSettings().update();
        player.sendAvailableCommands();
        player.sendPacket(new Packet1DUpdateAttributes(
                player.getEntityID(),
                player.getAttribute(Attribute.MAX_HEALTH),
                player.getAttribute(Attribute.MAX_HUNGER),
                player.getAttribute(Attribute.MOVEMENT_SPEED),
                player.getAttribute(Attribute.EXPERIENCE_LEVEL),
                player.getAttribute(Attribute.EXPERIENCE)
        ));
        this.server.getPlayerList().addPlayer(player);
        player.getWorld().addPlayerToWorld(player);
        PlayerJoinEvent joinEvent = new PlayerJoinEvent(player);
        this.server.getEventManager().call(joinEvent);
    }

    public LPlayer getPlayer(String name) {
        return this.playersByNames.get(name.toLowerCase());
    }

    public LPlayer getPlayer(UUID uuid) {
        return this.playersByUUIDs.get(uuid);
    }

    public LPlayer removePlayer(LPlayer player) {
        boolean wasOnline = player.isOnline();
        this.server.getScheduler().run(() -> {
            new PlayerDisconnectEvent(player).call();
            this.server.getPlayerList().removePlayer(player);
            this.playersByNames.remove(player.getName().toLowerCase());
            this.playersByUUIDs.remove(player.getUUID());
            if (wasOnline) {
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
        Set<LPlayer> players = new HashSet<>(getOnlinePlayers());
        for (LPlayer player : without)
            players.remove(player);
        return players;
    }

    public Collection<LPlayer> getAllPlayers() {
        return this.playersByUUIDs.values();
    }

    private void setupPlayerMetadata(LPlayer player) {
        player.setDisplayNameVisible(true, true);
        player.setDisplayName(player.getName());
        EntityMetadataHolder metadata = player.getMetadata();
        metadata.setDataFlag(false, EntityDataFlag.CAN_CLIMB, true, false);
        metadata.setDataFlag(false, EntityDataFlag.BREATHING, true, false);
        metadata.setDataFlag(false, EntityDataFlag.AFFECTED_BY_GRAVITY, true, false);
        metadata.setDataFlag(false, EntityDataFlag.HAS_COLLISION, true, false);
        metadata.setDirtyMetadata(false);
    }

    private void sendStartGamePacket(LPlayer player, Location spawnLocation) {
        Packet0BStartGame startGame = new Packet0BStartGame();
        startGame.entityId = startGame.runtimeEntityId = player.getEntityID();
        startGame.gamemode = startGame.worldGamemode = this.server.getServerSettings().getDefaultGamemode().ordinal();
        startGame.spawn = spawnLocation;
        startGame.seed = -1;
        startGame.dimension = spawnLocation.getWorld().getDimension().getId();
        startGame.difficulty = ((LWorld) spawnLocation.getWorld()).getDifficulty().ordinal();
        Location spawn = spawnLocation.getWorld().getSpawnLocation();
        startGame.worldSpawnX = spawn.getBlockX();
        startGame.worldSpawnY = spawn.getBlockY();
        startGame.worldSpawnZ = spawn.getBlockZ();
        startGame.hasAchievementsDisabled = true;
        startGame.dayCycleStopTime = -1;
        startGame.eduMode = false;
        startGame.rainLevel = 0;
        startGame.lightningLevel = 0;
        startGame.commandsEnabled = true;
        startGame.levelId = "";
        startGame.worldName = this.server.getServerSettings().getServerName();
        startGame.generator = 1; //0 old 1 infinity 2 flat
        player.sendPacket(startGame);
    }

}
