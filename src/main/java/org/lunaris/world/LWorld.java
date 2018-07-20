package org.lunaris.world;

import co.aikar.timings.Timings;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.lunaris.LunarisServer;
import org.lunaris.api.entity.Entity;
import org.lunaris.api.entity.Player;
import org.lunaris.api.event.chunk.ChunkLoadedEvent;
import org.lunaris.api.event.chunk.ChunkPreLoadEvent;
import org.lunaris.api.event.chunk.ChunkUnloadedEvent;
import org.lunaris.api.item.ItemStack;
import org.lunaris.api.material.Material;
import org.lunaris.api.util.math.Vector3d;
import org.lunaris.api.world.*;
import org.lunaris.block.BUFlag;
import org.lunaris.block.LBlock;
import org.lunaris.entity.LEntity;
import org.lunaris.entity.LPlayer;
import org.lunaris.network.packet.Packet15UpdateBlock;
import org.lunaris.network.packet.Packet18LevelSoundEvent;
import org.lunaris.util.math.LMath;
import org.lunaris.util.math.MathHelper;
import org.lunaris.world.format.test.TestChunk;
import org.lunaris.world.tileentity.LTileEntity;
import org.lunaris.world.tracker.EntityTracker;
import org.lunaris.world.util.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by RINES on 13.09.17.
 */
public class LWorld implements World {

    private final LunarisServer server;

    private final String name;

    private final Dimension dimension;

    private final Difficulty difficulty;

    private Location spawnLocation = new Location(this, 0D, 40D, 0D);

    private int time;

    private final Long2ObjectMap<LChunk> chunks = new Long2ObjectOpenHashMap<>();
    private final Set<LPlayer> players = new HashSet<>();
    private final Long2ObjectMap<LEntity> entities = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectMap<LTileEntity> tileEntities = new Long2ObjectOpenHashMap<>();

    private final ChunksFollowerTask followerTask;
    private final BlockUpdateScheduler blockUpdateScheduler;
    private final EntityTracker entityTracker;

    public LWorld(LunarisServer server, String name, Dimension dimension, Difficulty difficulty) {
        this.server = server;
        this.name = name;
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.entityTracker = new EntityTracker(server, this);
        if (server.getServerSettings().isUnloadChunks())
            server.getScheduler().scheduleRepeatable(new ChunkUnloaderTask(server, this, chunks), 30, 30, TimeUnit.SECONDS);
        blockUpdateScheduler = new BlockUpdateScheduler(this);
        this.followerTask = new ChunksFollowerTask(server, this);
    }

    public void addPlayerToWorld(LPlayer player) {
        if (this.players.contains(player))
            return;
        this.players.add(player);
        this.entities.put(player.getEntityID(), player);
        this.entityTracker.track(player);
        this.followerTask.updatePlayer(player);
        /*Collection<Player> without = getPlayersWithout(player);
        this.server.getNetworkManager().sendPacketImmediately(without, player.createSpawnPacket());
        without.stream().map(Player::createSpawnPacket).forEach(player::sendPacketImmediately);
        this.entities.values().stream()
                .filter(e -> e.getEntityType() != EntityType.PLAYER)
                .map(Entity::createSpawnPacket)
                .forEach(player::sendPacketImmediately);*/
    }

    public void removePlayerFromWorld(LPlayer player) {
        this.players.remove(player);
        removeEntityFromWorld(player);
    }

    public void addEntityToWorld(LEntity entity) {
        this.entities.put(entity.getEntityID(), entity);
        this.entityTracker.track(entity);
        //this.server.getNetworkManager().sendPacketImmediately(this.players, entity.createSpawnPacket());
    }

    public void removeEntityFromWorld(LEntity entity) {
        this.entities.remove(entity.getEntityID());
        this.entityTracker.untrack(entity);
        //this.server.getNetworkManager().sendPacketImmediately(this.players, new Packet0ERemoveEntity(entity.getEntityID()));
    }

    public void registerTileEntity(LTileEntity tileEntity) {
        this.tileEntities.put(hash(tileEntity.getLocation()), tileEntity);
    }

    public LTileEntity getTileEntityAt(int x, int y, int z) {
        return this.tileEntities.get(hash(x, y, z));
    }

    public LTileEntity getTileEntityAt(Vector3d position) {
        return this.tileEntities.get(hash(position));
    }

    public void unregisterTileEntity(LTileEntity tileEntity) {
        this.tileEntities.remove(hash(tileEntity.getLocation()));
    }

    public boolean isChunkLoadedAt(int x, int z) {
        return this.chunks.containsKey(hash(x, z));
    }

    public LBlock getBlockAt(Vector3d location) {
        return getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public LBlock getBlockAt(int x, int y, int z) {
        LChunk chunk = loadChunk(x >> 4, z >> 4);
        if (chunk == null)
            return new LBlock(new Location(this, x, y, z), Material.AIR, 0);
        return chunk.getBlock(x, y, z);
    }

    public void updateBlock(LBlock block, BUFlag.Set flags) {
        int x = block.getX(), y = block.getY(), z = block.getZ();
        LChunk chunk = loadChunk(x >> 4, z >> 4);
        if (chunk == null)
            return;
        int id = block.getTypeId();
        chunk.setBlock0(x, y, z, id, block.getData());
        if (flags.has(BUFlag.SEND_PACKET))
            chunk.sendPacket(new Packet15UpdateBlock(x, y, z, id, block.getData()));
        if (flags.has(BUFlag.UPDATE_NEIGHBORS))
            updateNeighbor(block);
    }

    public void updateNeighbor(LBlock block) {
        for (BlockFace face : BlockFace.values()) {
            LBlock side = block.getSide(face);
            side.getHandle().onNeighborBlockChange(side, block);
        }
    }

    public LChunk getChunkAt(int x, int z) {
        return this.chunks.get(hash(x, z));
    }

    public LChunk loadChunk(int x, int z) {
        LChunk chunk = getChunkAt(x, z);
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

    public void tick(long current, float dT) {
        Timings.getWorldTickTimer(this).startTiming();
        if (++this.time >= 24000)
            this.time = 0;
        Timings.getChunksTickTimer().startTiming();
        this.chunks.values().forEach(LChunk::tick);
        this.blockUpdateScheduler.tick();
        Timings.getChunksTickTimer().stopTiming();
        this.followerTask.tick();
        tickEntities(current, dT);
        this.tileEntities.values().forEach(LTileEntity::tick);
        Timings.getWorldTickTimer(this).stopTiming();
    }

    private void tickEntities(long current, float dT) {
        Timings.getEntitiesTickTimer().startTiming();
        for(LEntity entity : new ArrayList<>(this.entities.values()))
            entity.tick(current, dT);
        entityTracker.tick();
        Timings.getEntitiesTickTimer().stopTiming();
    }

    public void scheduleUpdate(LBlock block, int delay) {
        blockUpdateScheduler.scheduleUpdate(block, delay);
    }

    @Override
    public int getTime() {
        return this.time;
    }

    @Override
    public void dropItem(ItemStack itemStack, Vector3d position) {
        if(itemStack == null || itemStack.getType() == Material.AIR)
            throw new IllegalArgumentException("You can not drop air itemstack as item entity!");
        this.server.getEntityProvider().spawnItem(
                position instanceof Location
                        ? (Location) position
                        : new Location(this, position.getX(), position.getY(), position.getZ()),
                itemStack
        );
    }

    public LEntity getEntityById(long entityID) {
        return this.entities.get(entityID);
    }

    public Collection<LEntity> getEntities() {
        return this.entities.values();
    }

    @Override
    public Collection<LEntity> getNearbyEntities(Vector3d location, double radius) {
        return this.entities.values().stream().filter(e -> e.getLocation().distance(location) <= radius).collect(Collectors.toSet());
    }

    @Override
    public <T extends Entity> Collection<T> getNearbyEntitiesByClass(Class<T> entityClass, Vector3d location, double radius) {
        Set<T> result = new HashSet<>();
        for(LEntity entity : this.entities.values()) {
            if(entityClass.isAssignableFrom(entity.getClass()) && entity.getLocation().distance(location) <= radius)
                result.add((T) entity);
        }
        return result;
    }

    public <T extends Entity> Collection<T> getNearbyEntitiesByClass(Class<T> entityClass, Location location, double radiusXZ, double radiusY) {
        Set<T> result = new HashSet<>();
        radiusXZ *= radiusXZ;
        for(LEntity entity : this.entities.values()) {
            if(!entityClass.isAssignableFrom(entity.getClass()))
                continue;
            Location loc = entity.getLocation();
            if(Math.abs(loc.getY() - location.getY()) <= radiusY && LMath.pow2(loc.getX() - location.getX()) + LMath.pow2(loc.getZ() - location.getZ()) <= radiusXZ)
                result.add((T) entity);
        }
        return result;
    }

    Collection<LPlayer> getApplicablePlayers(LChunk chunk) {
        Set<LPlayer> players = new HashSet<>();
        for (LPlayer p : this.players)
            if (isInRangeOfView(p, chunk))
                players.add(p);
        return players;
    }

    @Override
    public Collection<LPlayer> getWatcherPlayers(Vector3d location) {
        Set<LPlayer> players = new HashSet<>();
        int x = location.getBlockX(), z = location.getBlockZ();
        for (LPlayer p : this.players)
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

    public boolean isInRangeOfView(Vector3d location, LChunk chunk) {
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

    private long hash(int x, int y, int z) {
        return LongHash.toHash(x, y, z);
    }

    private long hash(Vector3d position) {
        return hash(position.getBlockX(), position.getBlockY(), position.getBlockZ());
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
    
    public EntityTracker getEntityTracker() {
        return entityTracker;
    }

    public Collection<LPlayer> getPlayers() {
        return this.players;
    }

    public Collection<LPlayer> getPlayersWithout(LPlayer p) {
        Set<LPlayer> players = new HashSet<>();
        players.addAll(this.players);
        players.remove(p);
        return players;
    }

    public Collection<LChunk> getLoadedChunks() {
        return this.chunks.values();
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public void playSound(Sound sound, Location loc, float pitch) {
        this.server.getNetworkManager().sendPacket(getWatcherPlayers(loc), new Packet18LevelSoundEvent(sound, loc, -1, (int) (pitch / 1000f), false, false));
    }

    public void playSound(Sound sound) {
        getPlayers().forEach(p -> p.playSound(sound, p.getLocation()));
    }

    @Override
    public String toString() {
        return "World(name=" + this.name + ")";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(!(o instanceof LWorld))
            return false;
        return this.name.equals(((LWorld) o).name);
    }

}
