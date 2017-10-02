package org.lunaris.world;

import co.aikar.timings.Timings;

import org.lunaris.Lunaris;
import org.lunaris.block.BUFlag;
import org.lunaris.block.Block;
import org.lunaris.block.BlockFace;
import org.lunaris.entity.Entity;
import org.lunaris.entity.Player;
import org.lunaris.event.chunk.ChunkLoadedEvent;
import org.lunaris.event.chunk.ChunkPreLoadEvent;
import org.lunaris.event.chunk.ChunkUnloadedEvent;
import org.lunaris.material.Material;
import org.lunaris.network.protocol.packet.Packet0CAddPlayer;
import org.lunaris.network.protocol.packet.Packet0ERemoveEntity;
import org.lunaris.network.protocol.packet.Packet15UpdateBlock;
import org.lunaris.network.protocol.packet.Packet18LevelSoundEvent;
import org.lunaris.util.math.MathHelper;
import org.lunaris.util.math.Vector3d;
import org.lunaris.world.format.test.TestChunk;
import org.lunaris.world.util.BlockUpdateScheduler;
import org.lunaris.world.util.ChunkUnloaderTask;
import org.lunaris.world.util.ChunksFollowerTask;
import org.lunaris.world.util.LongHash;
import org.lunaris.world.util.LongObjectHashMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
    private final Set<Entity> entities = new HashSet<>();

    private final ChunksFollowerTask followerTask;
    private final BlockUpdateScheduler blockUpdateScheduler;

    public World(Lunaris server, String name, Dimension dimension, Difficulty difficulty) {
        this.server = server;
        this.name = name;
        this.dimension = dimension;
        this.difficulty = difficulty;
        if (server.getServerSettings().isUnloadChunks())
            server.getScheduler().scheduleRepeatable(new ChunkUnloaderTask(server, this, chunks), 30, 30, TimeUnit.SECONDS);
        blockUpdateScheduler = new BlockUpdateScheduler(this);
        this.followerTask = new ChunksFollowerTask(server, this);
    }

    public void addPlayerToWorld(Player player) {
        if (this.players.contains(player))
            return;
        this.players.add(player);
        this.entities.add(player);
        this.followerTask.updatePlayer(player);
        Collection<Player> without = getPlayersWithout(player);
        this.server.getNetworkManager().sendPacket(without, new Packet0CAddPlayer(player));
        without.stream().map(Packet0CAddPlayer::new).forEach(player::sendPacket);
        //        this.server.getNetworkManager().sendPacket(without, new Packet27SetEntityData(player.getEntityID(), player.getDataProperties()));
    }

    public void removePlayerFromWorld(Player player) {
        this.players.remove(player);
        removeEntityFromWorld(player);
    }

    public void addEntityToWorld(Entity entity) {
        this.entities.add(entity);
        //send add entity packet
    }

    public void removeEntityFromWorld(Entity entity) {
        this.entities.remove(entity);
        this.server.getNetworkManager().sendPacket(getPlayers(), new Packet0ERemoveEntity(entity.getEntityID()));
    }

    public boolean isChunkLoadedAt(int x, int z) {
        return this.chunks.containsKey(hash(x, z));
    }

    public Block getBlockAt(Vector3d location) {
        return getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Block getBlockAt(int x, int y, int z) {
        Chunk chunk = loadChunk(x >> 4, z >> 4);
        if (chunk == null)
            return new Block(new Location(this, x, y, z), Material.AIR, 0);
        return chunk.getBlock(x, y, z);
    }

    public void updateBlock(Block block, BUFlag.Set flags) {
        int x = block.getX(), y = block.getY(), z = block.getZ();
        Chunk chunk = loadChunk(x >> 4, z >> 4);
        if (chunk == null)
            return;
        int id = block.getTypeId();
        chunk.setBlock0(x, y, z, id, block.getData());
        if (flags.has(BUFlag.SEND_PACKET))
            chunk.sendPacket(new Packet15UpdateBlock(x, y, z, id, block.getData()));
        if (flags.has(BUFlag.UPDATE_NEIGHBORS))
            updateNeighbor(block);
    }

    public void updateNeighbor(Block block) {
        System.out.println("UPD "+block);
        for (BlockFace face : BlockFace.values()) {
            Block side = block.getSide(face);
            side.getHandle().onNeighborBlockChange(side, block);
        }
    }

    public Chunk getChunkAt(int x, int z) {
        return this.chunks.get(hash(x, z));
    }

    public Chunk loadChunk(int x, int z) {
        Chunk chunk = getChunkAt(x, z);
        if (chunk != null)
            return chunk;
        ChunkPreLoadEvent preloadEvent = new ChunkPreLoadEvent(x, z);
        this.server.getEventManager().call(preloadEvent);
        if (preloadEvent.isCancelled())
            return null;
        chunk = new TestChunk(this, x, z);
        this.server.getScheduler().runAsync(chunk::load);
        this.chunks.put(hash(x, z), chunk); //NPE
        ChunkLoadedEvent loadedEvent = new ChunkLoadedEvent(chunk);
        this.server.getEventManager().call(loadedEvent);
        //System.out.println("Chunk " + chunk.getX() + " " + chunk.getZ() + " load initiated");
        return chunk;
    }

    public void unloadChunk(Chunk chunk) {
        if (chunk == null)
            return;
        ChunkUnloadedEvent unloadedEvent = new ChunkUnloadedEvent(chunk);
        this.server.getEventManager().call(unloadedEvent);
        this.chunks.remove(hash(chunk.getX(), chunk.getZ()));
        //System.out.println("Chunk " + chunk.getX() + " " + chunk.getZ() + " unloaded");
    }

    public void unloadChunk(int x, int z) {
        unloadChunk(getChunkAt(x, z));
    }

    public void tick() {
        Timings.getWorldTickTimer(this).startTiming();
        if (++this.time >= 24000)
            this.time = 0;
        Timings.getChunksTickTimer().startTiming();
        this.chunks.values().forEach(Chunk::tick);
        this.blockUpdateScheduler.tick();
        Timings.getChunksTickTimer().stopTiming();
        this.followerTask.tick();
        Timings.getEntitiesTickTimer().startTiming();
        this.entities.forEach(Entity::tick);
        Timings.getEntitiesTickTimer().stopTiming();
        Timings.getWorldTickTimer(this).stopTiming();
    }

    public void scheduleUpdate(Block block, int delay) {
        blockUpdateScheduler.scheduleUpdate(block, delay);
    }

    public int getTime() {
        return this.time;
    }

    Collection<Player> getApplicablePlayers(Chunk chunk) {
        Set<Player> players = new HashSet<>();
        for (Player p : this.players)
            if (isInRangeOfView(p, chunk))
                players.add(p);
        return players;
    }

    public Collection<Player> getApplicablePlayers(Vector3d location) {
        Set<Player> players = new HashSet<>();
        int x = location.getBlockX(), z = location.getBlockZ();
        for (Player p : this.players)
            if (isInRangeOfView(p, x, z))
                players.add(p);
        return players;
    }

    public boolean isInRangeOfView(Player player, int x, int z) {
        return isInRangeOfView(player.getLocation(), x >> 4, z >> 4, player.getChunksView());
    }

    public boolean isInRangeOfView(Player player, Chunk chunk) {
        return isInRangeOfView(player.getLocation(), chunk.getX(), chunk.getZ(), player.getChunksView());
    }

    public boolean isInRangeOfViewChunk(Player player, int x, int z) {
        return isInRangeOfView(player.getLocation(), x, z, player.getChunksView());
    }

    public boolean isInRangeOfView(Vector3d location, Chunk chunk) {
        return isInRangeOfView(location, chunk.getX(), chunk.getZ(), this.server.getServerSettings().getChunksView());
    }

    public boolean isInRangeOfView(Vector3d location, int x, int z, int range) {
        return MathHelper.pow2((location.getBlockX() >> 4) - x)
            + MathHelper.pow2((location.getBlockZ() >> 4) - z)
            <= MathHelper.pow2(range);
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

    public Collection<Chunk> getLoadedChunks() {
        return this.chunks.values();
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public void playSound(Sound sound, Location loc, float pitch) {
        this.server.getNetworkManager().sendPacket(getApplicablePlayers(loc), new Packet18LevelSoundEvent(sound, loc, -1, (int) (pitch / 1000f), false, false));
    }

    public void playSound(Sound sound) {
        getPlayers().forEach(p -> p.playSound(sound, p.getLocation()));
    }

    @Override
    public String toString() {
        return "World(name=" + this.name + ")";
    }
}
