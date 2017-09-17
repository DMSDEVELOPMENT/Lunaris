package org.lunaris.world;

import co.aikar.timings.Timings;
import org.lunaris.Lunaris;
import org.lunaris.block.Block;
import org.lunaris.block.Material;
import org.lunaris.entity.Player;
import org.lunaris.event.chunk.ChunkLoadedEvent;
import org.lunaris.event.chunk.ChunkPreLoadEvent;
import org.lunaris.event.chunk.ChunkUnloadedEvent;
import org.lunaris.network.protocol.packet.Packet0CAddPlayer;
import org.lunaris.network.protocol.packet.Packet0ERemoveEntity;
import org.lunaris.network.protocol.packet.Packet27SetEntityData;
import org.lunaris.server.IServer;
import org.lunaris.util.math.Vector3d;
import org.lunaris.world.format.test.TestChunk;
import org.lunaris.world.util.ChunksFollowerTask;
import org.lunaris.world.util.KillerTask;
import org.lunaris.world.util.LongHash;
import org.lunaris.world.util.LongObjectHashMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by RINES on 13.09.17.
 */
public class World {

    private final Lunaris server;

    private final String name;

    private final Dimension dimension;

    private final Difficulty difficulty;

    private Location spawnLocation = new Location(this, 0D, 40D, 0D);

    private int time;

    private final LongObjectHashMap<Chunk> chunks = new LongObjectHashMap<>();
    private final Set<Player> players = new HashSet<>();

    private final KillerTask killerTask;
    private final ChunksFollowerTask followerTask;

    public World(Lunaris server, String name, Dimension dimension, Difficulty difficulty) {
        this.server = server;
        this.name = name;
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.killerTask = server.getServerSettings().isUnloadChunks() ? new KillerTask(server, this, this.chunks) : null;
        this.followerTask = new ChunksFollowerTask(server, this);
    }

    public void addPlayerToWorld(Player player) {
        if(this.players.contains(player))
            return;
        this.players.add(player);
        Location loc = player.getLocation();
        int cx = loc.getBlockX() >> 4, cz = loc.getBlockZ() >> 4;
        int r = player.getChunksView();
        for(int x = cx - r; x <= cx + r; ++x)
            for(int z = cz - r; z <= cz + r; ++z) {
                Chunk chunk = loadChunk(x, z);
                if(chunk != null)
                    chunk.sendTo(player);
            }
        Collection<Player> without = getPlayersWithout(player);
        this.server.getNetworkManager().sendPacket(without, new Packet0CAddPlayer(player));
        without.stream().map(Packet0CAddPlayer::new).forEach(player::sendPacket);
//        this.server.getNetworkManager().sendPacket(without, new Packet27SetEntityData(player.getEntityID(), player.getDataProperties()));
    }

    public void removePlayerFromWorld(Player player) {
        this.players.remove(player);
        this.server.getNetworkManager().sendPacket(getPlayers(), new Packet0ERemoveEntity(player.getEntityID()));
    }

    public synchronized boolean isChunkLoadedAt(int x, int z) {
        return this.chunks.containsKey(hash(x, z));
    }

    public Block getBlockAt(Vector3d location) {
        return getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Block getBlockAt(int x, int y, int z) {
        Chunk chunk = loadChunk(x >> 4, z >> 4);
        if(chunk == null)
            return new Block(new Location(this, x, y, z), Material.AIR, 0);
        return chunk.getBlock(x, y, z);
    }

    public synchronized Chunk getChunkAt(int x, int z) {
        return this.chunks.get(hash(x, z));
    }

    public synchronized Chunk loadChunk(int x, int z) {
        Chunk chunk = getChunkAt(x, z);
        if(chunk != null)
            return chunk;
        ChunkPreLoadEvent preloadEvent = new ChunkPreLoadEvent(x, z);
        this.server.getEventManager().call(preloadEvent);
        if(preloadEvent.isCancelled())
            return null;
        chunk = new TestChunk(this, x, z);
        this.chunks.put(hash(x, z), chunk); //NPE
        ChunkLoadedEvent loadedEvent = new ChunkLoadedEvent(chunk);
        this.server.getEventManager().call(loadedEvent);
        return chunk;
    }

    public synchronized  void unloadChunk(Chunk chunk) {
        if(chunk == null)
            return;
        ChunkUnloadedEvent unloadedEvent = new ChunkUnloadedEvent(chunk);
        this.server.getEventManager().call(unloadedEvent);
        this.chunks.remove(hash(chunk.getX(), chunk.getZ()));
    }

    public void unloadChunk(int x, int z) {
        unloadChunk(getChunkAt(x, z));
    }

    public void tick() {
        if(++this.time >= 24000)
            this.time = 0;
        Timings.chunksTickTimer.startTiming();
        this.chunks.values().forEach(Chunk::tick);
        Timings.chunksTickTimer.stopTiming();
        if(this.killerTask != null)
            this.killerTask.tick();
        this.followerTask.tick();
        Timings.playersTickTimer.startTiming();
        this.players.forEach(Player::tick);
        Timings.playersTickTimer.stopTiming();
    }

    public int getTime() {
        return this.time;
    }

    Collection<Player> getApplicablePlayers(Chunk chunk) {
        Set<Player> players = new HashSet<>();
        for(Player p : this.players)
            if(isInRangeOfView(p, chunk))
                players.add(p);
        return players;
    }

    public Collection<Player> getApplicablePlayers(Vector3d location) {
        Set<Player> players = new HashSet<>();
        double x = location.getX(), z = location.getZ();
        for(Player p : this.players)
            if(isInRangeOfView(p, x, z))
                players.add(p);
        return players;
    }

    public boolean isInRangeOfView(Player player, double x, double z) {
        return Math.hypot(player.getLocation().getX() - x, player.getLocation().getZ() - z) <= player.getChunksView() << 4;
    }

    public boolean isInRangeOfView(Player player, Chunk chunk) {
        return isInRangeOfView(player.getLocation(), chunk);
    }

    public boolean isInRangeOfView(Vector3d location, Chunk chunk) {
        double x = location.getX(), z = location.getZ();
        return Math.hypot(x - (chunk.getX() << 4), z - (chunk.getZ() << 4)) <= (this.server.getServerSettings().getChunksView() + 1) << 4;
    }

    private long hash(int x, int z) {
        return LongHash.toLong(x, z);
    }

    public String getName() {
        return this.name;
    }

    public Dimension getDimension() {
        return this.dimension;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public Location getSpawnLocation() {
        return this.spawnLocation.clone();
    }

    public Collection<Player> getPlayers() {
        return this.players;
    }

    public Collection<Player> getPlayersWithout(Player p) {
        Set<Player> players = new HashSet<>();
        players.addAll(this.players);
        players.remove(p);
        return players;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }
}
