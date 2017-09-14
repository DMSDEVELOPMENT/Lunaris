package org.lunaris.server;

import org.lunaris.entity.Player;
import org.lunaris.event.player.PlayerDisconnectEvent;
import org.lunaris.event.player.PlayerJoinEvent;
import org.lunaris.event.player.PlayerLoginEvent;
import org.lunaris.nbt.tag.CompoundTag;
import org.lunaris.nbt.tag.DoubleTag;
import org.lunaris.nbt.tag.FloatTag;
import org.lunaris.nbt.tag.ListTag;
import org.lunaris.network.protocol.packet.*;
import org.lunaris.network.raknet.session.RakNetClientSession;
import org.lunaris.world.Location;
import org.lunaris.world.Position;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
        setupPlayer(player);
        PlayerLoginEvent event = new PlayerLoginEvent(player);
        this.server.getEventManager().call(event);
        if(event.isCancelled()) {
            player.disconnect();
            return;
        }
        player.setIngameState(Player.IngameState.ONLINE);
        this.playersByNames.put(player.getName(), player);
        this.playersByUUIDs.put(player.getClientUUID(), player);
        this.server.getWorldProvider().getWorld(0).loadChunk(0, 0);
        Location loc = player.getLocation();
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
        player.sendPacket(startGame);
        player.sendPacket(new Packet0ASetTime(loc.getWorld().getTime()));
        player.sendPacket(new Packet2DRespawn((float) loc.getX(), (float) loc.getY(), (float) loc.getZ()));
        player.sendPacket(new Packet02PlayStatus(Packet02PlayStatus.Status.PLAYER_RESPAWN));
        player.getWorld().addPlayerToWorld(player);
        PlayerJoinEvent joinEvent = new PlayerJoinEvent(player);
        this.server.getEventManager().call(joinEvent);
    }

    private void setupPlayer(Player player) {
        Location spawn = this.server.getWorldProvider().getWorld(0).getSpawnLocation();
        System.out.println((spawn == null) + " " + (this.server.getWorldProvider().getWorld(0) == null));
        CompoundTag nbt = new CompoundTag()
                .putLong("firstPlayed", System.currentTimeMillis() / 1000)
                .putLong("lastPlayed", System.currentTimeMillis() / 1000)
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("0", spawn.x))
                        .add(new DoubleTag("1", spawn.y))
                        .add(new DoubleTag("2", spawn.z)))
                .putString("Level", this.server.getWorldProvider().getWorld(0).getName())
                .putList(new ListTag<>("Inventory"))
                .putCompound("Achievements", new CompoundTag())
                .putInt("playerGameType", this.server.getServerSettings().getDefaultGamemode().ordinal())
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("0", 0))
                        .add(new DoubleTag("1", 0))
                        .add(new DoubleTag("2", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("0", 0))
                        .add(new FloatTag("1", 0)))
                .putFloat("FallDistance", 0)
                .putShort("Fire", 0)
                .putShort("Air", 300)
                .putBoolean("OnGround", true)
                .putBoolean("Invulnerable", false)
                .putString("NameTag", player.getName());
        if(!nbt.contains("foodLevel"))
            nbt.putInt("foodLevel", 20);
        if(!nbt.contains("FoodSaturationLevel"))
            nbt.putFloat("FoodSaturationLevel", 20);
        player.setNbt(nbt);
        player.setLocation(spawn);
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
